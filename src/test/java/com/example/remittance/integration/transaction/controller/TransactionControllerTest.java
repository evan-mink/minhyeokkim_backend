package com.example.remittance.integration.transaction.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.remittance.common.model.base.BaseApiRequest;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.common.model.entity.Account;
import com.example.remittance.common.model.entity.AccountLimit;
import com.example.remittance.common.model.entity.AccountTransaction;
import com.example.remittance.common.model.entity.TransactionFee;
import com.example.remittance.common.model.entity.id.AccountLimitId;
import com.example.remittance.common.model.type.CommonType;
import com.example.remittance.common.repository.AccountLimitRepository;
import com.example.remittance.common.repository.AccountRepository;
import com.example.remittance.common.repository.AccountTransactionRepository;
import com.example.remittance.common.repository.TransactionFeeRepository;
import com.example.remittance.common.service.CodeService;
import com.example.remittance.transaction.model.TransactionDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 거래 정보 rest controller API test
 * @author evan.m.kim
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // IntelliJ version issue
@SpringBootTest(properties = "spring.profiles.active:local")
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CodeService codeService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountLimitRepository accountLimitRepository;

	@Autowired
	private AccountTransactionRepository accountTransactionRepository;

	@Autowired
	private TransactionFeeRepository transactionFeeRepository;

	/***
	 * 거래 리스트 조회 test
	 * @throws Exception
	 */
	@Test
	public void selectTransactionList() throws Exception {
		// when & then
		mockMvc.perform(get("/transactions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(BaseApiResponse.ok().isSuccess()))
				.andExpect(jsonPath("$.code").value(BaseApiResponse.ok().getCode()))
				.andExpect(jsonPath("$.message").value(BaseApiResponse.ok().getMessage()))
				.andExpect(jsonPath("$.data").isArray());
	}

	/***
	 * 거래 조회 test
	 * @throws Exception
	 */
	@Test
	public void selectTransaction() throws Exception {
		// given
		AccountTransaction accountTransaction = accountTransactionRepository.findAll()
				.stream()
				.findFirst()
				.orElse(null);

		if (accountTransaction != null) {
			// when
			MvcResult mvcResult =
					mockMvc.perform(get("/transactions/{transactionId}", accountTransaction.getTransactionId())
							.contentType(MediaType.APPLICATION_JSON))
							.andReturn();

			String responseBodyJson = mvcResult.getResponse().getContentAsString();

			BaseApiResponse<TransactionDto.Response> responseBody =
					new ObjectMapper().readValue(responseBodyJson,
							new TypeReference<BaseApiResponse<TransactionDto.Response>>() {});

			// then
			// - response info
			assertEquals(BaseApiResponse.ok().isSuccess(), responseBody.isSuccess());
			assertEquals(BaseApiResponse.ok().getCode(), responseBody.getCode());
			assertEquals(BaseApiResponse.ok().getMessage(), responseBody.getMessage());

			// - response data
			TransactionDto.Response actual = responseBody.getData();
			assertNotNull(actual);

			AccountTransaction expected = accountTransaction;
			assertNotNull(expected);
			assertNotNull(expected.getCodeTransaction());
			assertNotNull(expected.getAccount());
			assertNotNull(expected.getAccount().getCodeBank());

			assertEquals(expected.getTransactionId(), actual.getTransactionId());
			assertEquals(expected.getCodeTransaction().getTransactionCode(), actual.getTransactionCode());
			assertEquals(expected.getCodeTransaction().getDescription(), actual.getDescription());
			assertEquals(expected.getAccount().getAccountId(), actual.getAccountId());
			assertEquals(expected.getAccount().getAccountNumber(), actual.getAccountNumber());
			assertEquals(expected.getAccount().getCodeBank().getBankName(), actual.getBankName());
			assertEquals(expected.getAmount(), actual.getAmount());

			// -- '이체' 인 경우 대상 계좌 정보 비교
			String transactionCode = expected.getCodeTransaction().getTransactionCode();

			if (transactionCode.equals(CommonType.TransactionCode.TRANSFER.getCode())) {
				assertNotNull(expected.getTargetAccount());
				assertNotNull(expected.getTargetAccount().getCodeBank());

				assertEquals(expected.getTargetAccount().getAccountId(), actual.getTargetAccountId());
				assertEquals(expected.getTargetAccount().getAccountNumber(), actual.getTargetAccountNumber());
				assertEquals(expected.getTargetAccount().getCodeBank().getBankName(), actual.getTargetBankName());
			}
		} else {
			// when & then
			mockMvc.perform(get("/transactions/{transactionId}", 0L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.success").value(BaseApiResponse.ok().isSuccess()))
					.andExpect(jsonPath("$.code").value(BaseApiResponse.ok().getCode()))
					.andExpect(jsonPath("$.message").value(BaseApiResponse.ok().getMessage()))
					.andExpect(jsonPath("$.data").isEmpty());
		}
	}

	/***
	 * 입금 - request body 누락
	 * @throws Exception
	 */
	@Test
	public void deposit_invalidRequestBody() throws Exception {
		// given
		BaseApiRequest<TransactionDto.TransactionRequest> requestBody = new BaseApiRequest<>();

		// when & then
		mockMvc.perform(post("/transactions/deposit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(requestBody)))
				.andExpect(jsonPath("$.success").value(BaseApiResponse.error().isSuccess()))
				.andExpect(jsonPath("$.code").value(BaseApiResponse.error().getCode()))
				.andExpect(jsonPath("$.message").value("invalid request body"));
	}

	/***
	 * 입금 - 정상 test
	 * @throws Exception
	 */
	@Test
	public void deposit_valid() throws Exception {
		// given
		Account account = accountRepository.findAllByDelYn(0)
				.stream()
				.findFirst()
				.orElse(null);
		// - 금액
		BigDecimal amount = BigDecimal.valueOf(100000);
		// - 잔액
		BigDecimal balance = (account != null) ? account.getBalance() : BigDecimal.ZERO;

		BaseApiRequest<TransactionDto.TransactionRequest> requestBody = new BaseApiRequest<>(
				TransactionDto.TransactionRequest.builder()
						.accountId((account != null) ? account.getAccountId() : 0L)
						.amount(amount)
						.build()
		);

		ObjectMapper objectMapper = new ObjectMapper();

		// when
		MvcResult mvcResult = mockMvc.perform(post("/transactions/deposit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBody)))
				.andReturn();

		String responseBodyJson = mvcResult.getResponse().getContentAsString();

		BaseApiResponse<TransactionDto.Response> responseBody =
				objectMapper.readValue(responseBodyJson,
						new TypeReference<BaseApiResponse<TransactionDto.Response>>() {});

		// then
		if (account != null) {
			if (responseBody.isSuccess()) {
				// - response info
				assertEquals(BaseApiResponse.ok().isSuccess(), responseBody.isSuccess());
				assertEquals(BaseApiResponse.ok().getCode(), responseBody.getCode());
				assertEquals(BaseApiResponse.ok().getMessage(), responseBody.getMessage());

				// - response data
				TransactionDto.Response actual = responseBody.getData();
				assertNotNull(actual);

				AccountTransaction expected =
						accountTransactionRepository.findById(actual.getTransactionId()).orElse(null);
				assertNotNull(expected);
				assertNotNull(expected.getCodeTransaction());
				assertNotNull(expected.getAccount());
				assertNotNull(expected.getAccount().getCodeBank());

				assertEquals(expected.getTransactionId(), actual.getTransactionId());
				assertEquals(expected.getCodeTransaction().getTransactionCode(), actual.getTransactionCode());
				assertEquals(expected.getCodeTransaction().getDescription(), actual.getDescription());
				assertEquals(expected.getAccount().getAccountId(), actual.getAccountId());
				assertEquals(expected.getAccount().getAccountNumber(), actual.getAccountNumber());
				assertEquals(expected.getAccount().getCodeBank().getBankName(), actual.getBankName());
				assertEquals(0, expected.getAmount().compareTo(actual.getAmount()));
				assertEquals(expected.getAccount().getBalance(), balance.add(actual.getAmount()));
			} else {
				// - validate 검증
				assertEquals(BaseApiResponse.error().isSuccess(), responseBody.isSuccess());
				assertEquals(BaseApiResponse.error().getCode(), responseBody.getCode());
				assertEquals("invalid amount", responseBody.getMessage());
			}
		} else {
			// - validate 검증
			assertEquals(BaseApiResponse.error().isSuccess(), responseBody.isSuccess());
			assertEquals(BaseApiResponse.error().getCode(), responseBody.getCode());
			assertEquals("invalid account id", responseBody.getMessage());
		}
	}

	/***
	 * 출금 - request body 누락 test
	 * @throws Exception
	 */
	@Test
	public void withdraw_invalidRequestBody() throws Exception {
		// given
		BaseApiRequest<TransactionDto.TransactionRequest> requestBody = new BaseApiRequest<>();

		// when & then
		mockMvc.perform(post("/transactions/withdraw")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(requestBody)))
				.andExpect(jsonPath("$.success").value(BaseApiResponse.error().isSuccess()))
				.andExpect(jsonPath("$.code").value(BaseApiResponse.error().getCode()))
				.andExpect(jsonPath("$.message").value("invalid request body"));
	}

	/***
	 * 출금 - 정상 test
	 * @throws Exception
	 */
	@Test
	public void withdraw_valid() throws Exception {
		// given
		String transactionCode = CommonType.TransactionCode.WITHDRAW.getCode();

		Account account = accountRepository.findAllByDelYn(0)
				.stream()
				.findFirst()
				.orElse(null);

		// - 금액
		BigDecimal amount = BigDecimal.valueOf(1000000);

		// - 일 사용 금액
		BigDecimal dailyAmount = BigDecimal.ZERO;

		// - 잔액
		BigDecimal balance = BigDecimal.ZERO;

		// - 일 출금 한도
		BigDecimal dailyLimit = codeService.getCodeTransactionMap()
				.get(transactionCode)
				.getDailyLimit();

		if (account != null) {
			balance = account.getBalance();

			dailyAmount = accountLimitRepository.findById(
					AccountLimitId.builder()
							.accountId(account.getAccountId())
							.transactionCode(transactionCode)
							.build())
					.map(AccountLimit::getDailyAmount)
					.orElse(BigDecimal.ZERO);
		}

		BaseApiRequest<TransactionDto.TransactionRequest> requestBody = new BaseApiRequest<>(
				TransactionDto.TransactionRequest.builder()
						.accountId((account != null) ? account.getAccountId() : 0L)
						.amount(amount)
						.build()
		);

		ObjectMapper objectMapper = new ObjectMapper();

		// when
		MvcResult mvcResult = mockMvc.perform(post("/transactions/withdraw")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBody)))
				.andReturn();

		String responseBodyJson = mvcResult.getResponse().getContentAsString();

		BaseApiResponse<TransactionDto.Response> responseBody =
				objectMapper.readValue(responseBodyJson,
						new TypeReference<BaseApiResponse<TransactionDto.Response>>() {});

		// then
		if (account != null) {
			if (responseBody.isSuccess()) {
				// - response info
				assertEquals(BaseApiResponse.ok().isSuccess(), responseBody.isSuccess());
				assertEquals(BaseApiResponse.ok().getCode(), responseBody.getCode());
				assertEquals(BaseApiResponse.ok().getMessage(), responseBody.getMessage());

				// - response data
				TransactionDto.Response actual = responseBody.getData();
				assertNotNull(actual);

				AccountTransaction expected =
						accountTransactionRepository.findById(actual.getTransactionId()).orElse(null);
				assertNotNull(expected);
				assertNotNull(expected.getCodeTransaction());
				assertNotNull(expected.getAccount());
				assertNotNull(expected.getAccount().getCodeBank());

				assertEquals(expected.getTransactionId(), actual.getTransactionId());
				assertEquals(expected.getCodeTransaction().getTransactionCode(), actual.getTransactionCode());
				assertEquals(expected.getCodeTransaction().getDescription(), actual.getDescription());
				assertEquals(expected.getAccount().getAccountId(), actual.getAccountId());
				assertEquals(expected.getAccount().getAccountNumber(), actual.getAccountNumber());
				assertEquals(expected.getAccount().getCodeBank().getBankName(), actual.getBankName());
				assertEquals(0, expected.getAmount().compareTo(actual.getAmount()));
				assertEquals(expected.getAccount().getBalance(), balance.subtract(actual.getAmount()));
			} else {
				// - validate 검증
				assertEquals(BaseApiResponse.error().isSuccess(), responseBody.isSuccess());
				assertEquals(BaseApiResponse.error().getCode(), responseBody.getCode());

				if (amount.compareTo(BigDecimal.ZERO) <= 0) {
					// -- 금액 오류
					assertEquals("invalid amount", responseBody.getMessage());
				} else if (balance.compareTo(amount) < 0) {
					// -- 잔액 부족
					assertEquals("insufficient balance", responseBody.getMessage());
				} else if (dailyLimit.compareTo(dailyAmount.add(amount)) < 0) {
					// -- 한도 초과
					assertEquals("exceeded limit", responseBody.getMessage());
				}
			}
		} else {
			// - validate 검증
			assertEquals(BaseApiResponse.error().isSuccess(), responseBody.isSuccess());
			assertEquals(BaseApiResponse.error().getCode(), responseBody.getCode());
			assertEquals("invalid account id", responseBody.getMessage());
		}
	}

	/***
	 * 이체 - request body 누락 test
	 * @throws Exception
	 */
	@Test
	public void transfer_invalidRequestBody() throws Exception {
		// given
		BaseApiRequest<TransactionDto.TransferRequest> requestBody = new BaseApiRequest<>();

		// when & then
		mockMvc.perform(post("/transactions/transfer")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(requestBody)))
				.andExpect(jsonPath("$.success").value(BaseApiResponse.error().isSuccess()))
				.andExpect(jsonPath("$.code").value(BaseApiResponse.error().getCode()))
				.andExpect(jsonPath("$.message").value("invalid request body"));
	}

	/***
	 * 이체 - 정상 test
	 * @throws Exception
	 */
	@Test
	public void transfer_valid() throws Exception {
		// given
		String transactionCode = CommonType.TransactionCode.TRANSFER.getCode();

		List<Account> accountList = accountRepository.findAllByDelYn(0);
		// - 계좌
		Account account = (accountList.isEmpty())
				? null
				: accountList.get(0);

		// - 대상 계좌
		Account targetAccount = (accountList.isEmpty() || accountList.size() < 2)
				? null
				: accountList.get(1);

		// - 금액
		BigDecimal amount = BigDecimal.valueOf(1200000);

		// - 일 사용 금액
		BigDecimal dailyAmount = BigDecimal.ZERO;

		// - 잔액
		BigDecimal balance = BigDecimal.ZERO;

		// - 대상 계좌 잔액
		BigDecimal targetBalance = BigDecimal.ZERO;

		// - 일 출금 한도
		BigDecimal dailyLimit = codeService.getCodeTransactionMap()
				.get(transactionCode)
				.getDailyLimit();

		if (account != null) {
			balance = account.getBalance();

			dailyAmount = accountLimitRepository.findById(
					AccountLimitId.builder()
							.accountId(account.getAccountId())
							.transactionCode(transactionCode)
							.build())
					.map(AccountLimit::getDailyAmount)
					.orElse(BigDecimal.ZERO);
		}

		if (targetAccount != null) {
			targetBalance = targetAccount.getBalance();
		}

		BaseApiRequest<TransactionDto.TransferRequest> requestBody = new BaseApiRequest<>(
				TransactionDto.TransferRequest.builder()
						.accountId((account != null) ? account.getAccountId() : 0L)
						.targetAccountId((targetAccount != null) ? targetAccount.getAccountId() : 0L)
						.amount(amount)
						.build()
		);

		ObjectMapper objectMapper = new ObjectMapper();

		// when
		MvcResult mvcResult = mockMvc.perform(post("/transactions/transfer")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBody)))
				.andReturn();

		String responseBodyJson = mvcResult.getResponse().getContentAsString();

		BaseApiResponse<TransactionDto.Response> responseBody =
				objectMapper.readValue(responseBodyJson,
						new TypeReference<BaseApiResponse<TransactionDto.Response>>() {});

		// then
		if (account != null || targetAccount != null) {
			if (responseBody.isSuccess()) {
				// - response info
				assertEquals(BaseApiResponse.ok().isSuccess(), responseBody.isSuccess());
				assertEquals(BaseApiResponse.ok().getCode(), responseBody.getCode());
				assertEquals(BaseApiResponse.ok().getMessage(), responseBody.getMessage());

				// - response data
				TransactionDto.Response actual = responseBody.getData();
				assertNotNull(actual);

				AccountTransaction expected =
						accountTransactionRepository.findById(actual.getTransactionId()).orElse(null);
				assertNotNull(expected);
				assertNotNull(expected.getCodeTransaction());
				assertNotNull(expected.getAccount());
				assertNotNull(expected.getAccount().getCodeBank());

				assertEquals(expected.getTransactionId(), actual.getTransactionId());
				assertEquals(expected.getCodeTransaction().getTransactionCode(), actual.getTransactionCode());
				assertEquals(expected.getCodeTransaction().getDescription(), actual.getDescription());
				assertEquals(expected.getAccount().getAccountId(), actual.getAccountId());
				assertEquals(expected.getAccount().getAccountNumber(), actual.getAccountNumber());
				assertEquals(expected.getAccount().getCodeBank().getBankName(), actual.getBankName());
				assertEquals(expected.getTargetAccount().getAccountId(), actual.getTargetAccountId());
				assertEquals(expected.getTargetAccount().getAccountNumber(), actual.getTargetAccountNumber());
				assertEquals(expected.getTargetAccount().getCodeBank().getBankName(), actual.getTargetBankName());

				// -- 수수료
				BigDecimal feeAmount = transactionFeeRepository.findById(actual.getTransactionId())
						.map(TransactionFee::getFee)
						.orElse(BigDecimal.ZERO);

				assertEquals(0, expected.getAmount().compareTo(actual.getAmount()));

				assertEquals(expected.getTargetAccount().getBalance(), targetBalance.add(amount));
				assertEquals(expected.getAccount().getBalance(), balance.subtract(actual.getAmount().add(feeAmount)));

			} else {
				// - validate 검증
				assertEquals(BaseApiResponse.error().isSuccess(), responseBody.isSuccess());
				assertEquals(BaseApiResponse.error().getCode(), responseBody.getCode());

				if (amount.compareTo(BigDecimal.ZERO) <= 0) {
					// -- 금액 오류
					assertEquals("invalid amount", responseBody.getMessage());
				} else if (dailyLimit.compareTo(dailyAmount.add(amount)) < 0) {
					// -- 한도 초과
					assertEquals("exceeded limit", responseBody.getMessage());
				} else if (balance.compareTo(amount) < 0) {
					// -- 잔액 부족
					assertEquals("insufficient balance", responseBody.getMessage());
				}
			}
		} else {
			// - validate 검증
			assertEquals(BaseApiResponse.error().isSuccess(), responseBody.isSuccess());
			assertEquals(BaseApiResponse.error().getCode(), responseBody.getCode());

			if (account == null) {
				assertEquals("invalid account id", responseBody.getMessage());
			} else if (targetAccount == null) {
				assertEquals("invalid target account id", responseBody.getMessage());
			}
		}
	}
}
