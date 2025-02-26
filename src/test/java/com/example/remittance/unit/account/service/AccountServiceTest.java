package com.example.remittance.unit.account.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.remittance.account.model.AccountDto;
import com.example.remittance.account.service.AccountService;
import com.example.remittance.common.model.base.BaseApiRequest;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.common.model.entity.Account;
import com.example.remittance.common.model.entity.AccountLimit;
import com.example.remittance.common.model.entity.CodeBank;
import com.example.remittance.common.model.type.CommonType;
import com.example.remittance.common.repository.AccountLimitRepository;
import com.example.remittance.common.repository.AccountRepository;
import com.example.remittance.common.repository.CodeTransactionRepository;
import com.example.remittance.common.service.CodeService;
import com.example.remittance.common.util.RandomStringUtil;

/**
 * 계좌 service test
 * @author evan.m.kim
 */

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
	@InjectMocks
	private AccountService accountService;

	@Mock
	private CodeService codeService;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AccountLimitRepository accountLimitRepository;

	@Mock
	private CodeTransactionRepository codeTransactionRepository;

	/***
	 * 계좌 리스트 조회 test
	 */
	@Test
	public void selectAccountList() {
		// given
		List<Account> accountList = List.of(
				Account.builder()
						.accountId(1L)
						.codeBank(
								CodeBank.builder()
										.bankCode("003")
										.bankName("기업은행")
										.build()
						)
						.accountNumber("614-79-603038")
						.balance(BigDecimal.valueOf(950000))
						.delYn(0)
						.build(),
				Account.builder()
						.accountId(2L)
						.codeBank(
								CodeBank.builder()
										.bankCode("002")
										.bankName("산업은행")
										.build()
						)
						.accountNumber("125418063807897")
						.balance(BigDecimal.ZERO)
						.delYn(0)
						.build()
		);

		when(accountRepository.findAllByDelYn(0)).thenReturn(accountList);

		// when
		BaseApiResponse<List<AccountDto>> response = accountService.selectAccountList();
		List<AccountDto> actualList = response.getData();

		List<AccountDto> expectedList = List.of(
				AccountDto.builder()
						.accountId(1L)
						.bankCode("003")
						.bankName("기업은행")
						.accountNumber("614-79-603038")
						.balance(BigDecimal.valueOf(950000))
						.build(),
				AccountDto.builder()
						.accountId(2L)
						.bankCode("002")
						.bankName("산업은행")
						.accountNumber("125418063807897")
						.balance(BigDecimal.ZERO)
						.build()
		);

		// then
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());

		assertNotNull(actualList);
		assertEquals(expectedList.size(), actualList.size());

		for (int i = 0; i < expectedList.size(); i++) {
			AccountDto expected = expectedList.get(i);
			AccountDto actual = actualList.get(i);

			assertEquals(expected.getAccountId(), actual.getAccountId());
			assertEquals(expected.getBankCode(), actual.getBankCode());
			assertEquals(expected.getBankName(), actual.getBankName());
			assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
			assertTrue(expected.getBalance().compareTo(actual.getBalance()) == 0);
		}
	}

	/***
	 * 계좌 조회 - 계좌 연번 누락 test
	 */
	@Test
	public void selectAccount_invalidParam() {
		// given
		Long accountId = null;

		// when
		BaseApiResponse<AccountDto> response = accountService.selectAccount(accountId);

		// then
		assertEquals(BaseApiResponse.error().getCode(), response.getCode());
		assertEquals("invalid request param", response.getMessage());
	}

	/***
	 * 계좌 조회 - 정상 test
	 */
	@Test
	public void selectAccount_valid() {
		// given
		Account account = Account.builder()
				.accountId(1L)
				.codeBank(
						CodeBank.builder()
								.bankCode("003")
								.bankName("기업은행")
								.build()
				)
				.accountNumber("614-79-603038")
				.balance(BigDecimal.valueOf(950000))
				.delYn(0)
				.build();

		when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

		// when
		BaseApiResponse<AccountDto> response = accountService.selectAccount(account.getAccountId());

		AccountDto actual = response.getData();

		AccountDto expected = AccountDto.builder()
				.accountId(1L)
				.bankCode("003")
				.bankName("기업은행")
				.accountNumber("614-79-603038")
				.balance(BigDecimal.valueOf(950000))
				.build();

		// then
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());

		assertNotNull(actual);
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getBankCode(), actual.getBankCode());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
		assertTrue(expected.getBalance().compareTo(actual.getBalance()) == 0);
	}

	/***
	 * 계좌 조회 - 빈 결과 test
	 */
	@Test
	public void selectAccount_empty() {
		// given
		when(accountRepository.findById(2L)).thenReturn(Optional.empty());

		// then
		BaseApiResponse<AccountDto> response = accountService.selectAccount(2L);

		// when
		assertNull(response.getData());
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());
	}

	/***
	 * 계좌 등록 - request body 누락 test
	 */
	@Test
	public void insertAccount_invalidRequestBody() {
		// given
		BaseApiRequest<AccountDto> requestBody = new BaseApiRequest<>(null);

		// when
		BaseApiResponse<AccountDto> response = accountService.insertAccount(requestBody);

		// then
		assertEquals(BaseApiResponse.error().getCode(), response.getCode());
		assertEquals("invalid request body", response.getMessage());
	}

	/***
	 * 계좌 등록 - 은행 코드 오기입 test
	 */
	@Test
	public void insertAccount_invalidBankCode() {
		// given
		AccountDto accountDto = AccountDto.builder()
				.bankCode("invalid bank code")
				.build();

		BaseApiRequest<AccountDto> requestBody = new BaseApiRequest<>(accountDto);

		when(codeService.getCodeBankMap()).thenReturn(Collections.emptyMap());

		// when
		BaseApiResponse<AccountDto> response = accountService.insertAccount(requestBody);

		// then
		assertEquals(BaseApiResponse.error().getCode(), response.getCode());
		assertEquals("invalid bank code", response.getMessage());
	}

	/***
	 * 계좌 등록 - 정상 test
	 */
	@Test
	public void insertAccount_valid() {
		// given
		String bankCode = "003";

		AccountDto accountDto = AccountDto.builder()
				.bankCode(bankCode)
				.build();

		BaseApiRequest<AccountDto> requestBody = new BaseApiRequest<>(accountDto);

		CodeBank codeBank = CodeBank.builder()
				.bankCode(bankCode)
				.bankName("기업은행")
				.accountFormat("###-##-######")
				.build();

		String accountNumber = RandomStringUtil.generateAccountNumber(codeBank.getAccountFormat());

		Account account = Account.builder()
				.accountId(1L)
				.codeBank(codeBank)
				.accountNumber(accountNumber)
				.build();

		List<AccountLimit> accountLimitList = List.of(
				AccountLimit.builder()
						.accountId(account.getAccountId())
						.transactionCode(CommonType.TransactionCode.DEPOSIT.getCode())
						.dailyAmount(BigDecimal.ZERO)
						.build(),
				AccountLimit.builder()
						.accountId(account.getAccountId())
						.transactionCode(CommonType.TransactionCode.WITHDRAW.getCode())
						.dailyAmount(BigDecimal.ZERO)
						.build(),
				AccountLimit.builder()
						.accountId(account.getAccountId())
						.transactionCode(CommonType.TransactionCode.TRANSFER.getCode())
						.dailyAmount(BigDecimal.ZERO)
						.build()
		);

		when(codeService.getCodeBankMap()).thenReturn(Collections.singletonMap(bankCode, codeBank));
		when(accountRepository.existsByCodeBankAndAccountNumber(any(CodeBank.class), any(String.class))).thenReturn(false);
		when(accountRepository.save(any(Account.class))).thenReturn(account);
		when(codeTransactionRepository.findAll()).thenReturn(Collections.emptyList());
		when(accountLimitRepository.saveAll(any(List.class))).thenReturn(accountLimitList);
		when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

		// when
		BaseApiResponse<AccountDto> response = accountService.insertAccount(requestBody);

		AccountDto actual = response.getData();

		AccountDto expected = AccountDto.builder()
				.accountId(1L)
				.bankCode("003")
				.bankName("기업은행")
				.accountNumber(accountNumber)
				.balance(BigDecimal.ZERO)
				.build();

		// then
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());

		assertNotNull(actual);
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getBankCode(), actual.getBankCode());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
		assertTrue(expected.getBalance().compareTo(actual.getBalance()) == 0);
	}

	/***
	 * 계좌 삭제 - 계좌 연번 누락 test
	 */
	@Test
	public void deleteAccount_invalidRequestParam() {
		// given
		Long accountId = null;

		// when
		BaseApiResponse<Void> response = accountService.deleteAccount(accountId);

		// then
		assertEquals(BaseApiResponse.error().getCode(), response.getCode());
		assertEquals("invalid request param", response.getMessage());
	}

	/***
	 * 계좌 삭제 - 미등록 계좌 test
	 */
	@Test
	public void deleteAccount_invalidAccount() {
		// given
		Long accountId = 1L;

		when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

		// when
		BaseApiResponse<Void> response = accountService.deleteAccount(accountId);

		// then
		assertEquals(BaseApiResponse.error().getCode(), response.getCode());
		assertEquals("not found account", response.getMessage());
	}

	/***
	 * 계좌 삭제 - 정상 test
	 */
	@Test
	public void deleteAccount_valid() {
		// given
		Account account = Account.builder()
				.accountId(1L)
				.build();

		when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));
		when(accountRepository.save(account)).thenReturn(account);

		// when
		BaseApiResponse<Void> response = accountService.deleteAccount(account.getAccountId());

		// then
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());
		assertEquals(1, account.getDelYn());
	}
}