package com.example.remittance.integration.account.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.remittance.account.model.AccountDto;
import com.example.remittance.common.model.base.BaseApiRequest;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.common.model.entity.Account;
import com.example.remittance.common.repository.AccountRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 계좌 정보 rest controller API test
 * @author evan.m.kim
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // IntelliJ version issue
@SpringBootTest(properties = "spring.profiles.active:local")
@AutoConfigureMockMvc
@Transactional
public class AccountControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountRepository accountRepository;

	/***
	 * 계좌 리스트 조회 test
	 * @throws Exception
	 */
	@Test
	public void selectAccountList() throws Exception {
		// when & then
		mockMvc.perform(get("/accounts"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(BaseApiResponse.ok().isSuccess()))
				.andExpect(jsonPath("$.code").value(BaseApiResponse.ok().getCode()))
				.andExpect(jsonPath("$.message").value(BaseApiResponse.ok().getMessage()))
				.andExpect(jsonPath("$.data").isArray());
	}

	/***
	 * 계좌 조회 test
	 * @throws Exception
	 */
	@Test
	public void selectAccount() throws Exception {
		// given
		Account account = accountRepository.findAllByDelYn(0)
				.stream()
				.findFirst()
				.orElse(null);

		// when & then
		if (account != null) {
			mockMvc.perform(get("/accounts/{accountId}", account.getAccountId()))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.success").value(BaseApiResponse.ok().isSuccess()))
					.andExpect(jsonPath("$.code").value(BaseApiResponse.ok().getCode()))
					.andExpect(jsonPath("$.message").value(BaseApiResponse.ok().getMessage()))
					.andExpect(jsonPath("$.data.accountId").value(account.getAccountId()))
					.andExpect(jsonPath("$.data.bankCode").value(account.getCodeBank().getBankCode()))
					.andExpect(jsonPath("$.data.bankName").value(account.getCodeBank().getBankName()))
					.andExpect(jsonPath("$.data.accountNumber").value(account.getAccountNumber()))
					.andExpect(jsonPath("$.data.balance").value(account.getBalance().doubleValue()));
		} else {
			mockMvc.perform(get("/accounts/{accountId}", 0L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.success").value(BaseApiResponse.ok().isSuccess()))
					.andExpect(jsonPath("$.code").value(BaseApiResponse.ok().getCode()))
					.andExpect(jsonPath("$.message").value(BaseApiResponse.ok().getMessage()))
					.andExpect(jsonPath("$.data").isEmpty());
		}
	}

	/***
	 * 계좌 등록 - request body 누락 test
	 * @throws Exception
	 */
	@Test
	public void insertAccount_invalidRequestBody() throws Exception {
		// given
		BaseApiRequest<AccountDto> requestBody = new BaseApiRequest<>();

		// when & then
		mockMvc.perform(post("/accounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(requestBody)))
				.andExpect(jsonPath("$.success").value(BaseApiResponse.error().isSuccess()))
				.andExpect(jsonPath("$.code").value(BaseApiResponse.error().getCode()))
				.andExpect(jsonPath("$.message").value("invalid request body"));
	}

	/***
	 * 계좌 등록 - 은행 코드 오기입 test
	 * @throws Exception
	 */
	@Test
	public void insertAccount_invalidBankCode() throws Exception {
		// given
		BaseApiRequest<AccountDto> requestBody = new BaseApiRequest<>(
				AccountDto.builder()
						.bankCode("")
						.build()
		);

		// when & then
		mockMvc.perform(post("/accounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(requestBody)))
				.andExpect(jsonPath("$.success").value(BaseApiResponse.error().isSuccess()))
				.andExpect(jsonPath("$.code").value(BaseApiResponse.error().getCode()))
				.andExpect(jsonPath("$.message").value("invalid bank code"));
	}

	/***
	 * 계좌 등록 - 정상 test
	 * @throws Exception
	 */
	@Test
	public void insertAccount_valid() throws Exception {
		// given
		AccountDto accountDto = AccountDto.builder()
				.bankCode("003")
				.build();

		BaseApiRequest<AccountDto> requestBody = new BaseApiRequest<>(accountDto);

		// when
		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult mvcResult = mockMvc.perform(post("/accounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBody)))
				.andReturn();

		String responseBodyJson = mvcResult.getResponse().getContentAsString();

		BaseApiResponse<AccountDto> responseBody =
				objectMapper.readValue(responseBodyJson, new TypeReference<BaseApiResponse<AccountDto>>() {});

		// then
		// - response info
		assertEquals(BaseApiResponse.ok().isSuccess(), responseBody.isSuccess());
		assertEquals(BaseApiResponse.ok().getCode(), responseBody.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), responseBody.getMessage());

		// - response data
		AccountDto actual = responseBody.getData();
		assertNotNull(actual);

		Account expected = accountRepository.findById(actual.getAccountId()).orElse(null);
		assertNotNull(expected);

		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getCodeBank().getBankCode(), actual.getBankCode());
		assertEquals(expected.getCodeBank().getBankName(), actual.getBankName());
		assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
		assertEquals(expected.getBalance(), actual.getBalance());
	}

	/***
	 * 계좌 삭제
	 * @throws Exception
	 */
	@Test
	public void deleteAccount() throws Exception {
		// given
		Account account = accountRepository.findAllByDelYn(0)
				.stream()
				.findFirst()
				.orElse(null);

		// when & then
		if (account != null) {
			mockMvc.perform(delete("/accounts/{accountId}", account.getAccountId()))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.success").value(BaseApiResponse.ok().isSuccess()))
					.andExpect(jsonPath("$.code").value(BaseApiResponse.ok().getCode()))
					.andExpect(jsonPath("$.message").value(BaseApiResponse.ok().getMessage()))
					.andExpect(jsonPath("$.data").isEmpty());

			// - del_yn flag
			assertEquals(1, accountRepository.findById(account.getAccountId())
					.map(Account::getDelYn)
					.orElse(null)
			);
		} else {
			mockMvc.perform(delete("/accounts/{accountId}", 0L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.success").value(BaseApiResponse.error().isSuccess()))
					.andExpect(jsonPath("$.code").value(BaseApiResponse.error().getCode()))
					.andExpect(jsonPath("$.message").value("not found account"))
					.andExpect(jsonPath("$.data").isEmpty());
		}
	}
}
