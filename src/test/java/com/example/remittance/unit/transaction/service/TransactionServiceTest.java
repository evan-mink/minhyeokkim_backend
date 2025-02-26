package com.example.remittance.unit.transaction.service;

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

import com.example.remittance.common.model.base.BaseApiRequest;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.common.model.entity.Account;
import com.example.remittance.common.model.entity.AccountLimit;
import com.example.remittance.common.model.entity.AccountTransaction;
import com.example.remittance.common.model.entity.CodeBank;
import com.example.remittance.common.model.entity.CodeTransaction;
import com.example.remittance.common.model.entity.TransactionFee;
import com.example.remittance.common.model.type.CommonType;
import com.example.remittance.common.repository.AccountLimitRepository;
import com.example.remittance.common.repository.AccountRepository;
import com.example.remittance.common.repository.AccountTransactionRepository;
import com.example.remittance.common.repository.TransactionFeeRepository;
import com.example.remittance.common.service.CodeService;
import com.example.remittance.transaction.model.TransactionDto;
import com.example.remittance.transaction.service.TransactionService;

/**
 * 거래 service test
 * @author evan.m.kim
 */

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
	@InjectMocks
	private TransactionService transactionService;

	@Mock
	private CodeService codeService;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AccountLimitRepository accountLimitRepository;

	@Mock
	private AccountTransactionRepository accountTransactionRepository;

	@Mock
	private TransactionFeeRepository transactionFeeRepository;

	/***
	 * 거래 리스트 조회 test
	 */
	@Test
	public void selectTransactionList() {
		// given
		List<AccountTransaction> accountTransactionList = List.of(
				AccountTransaction
						.builder()
						.transactionId(3L)
						.codeTransaction(
								CodeTransaction.builder()
										.transactionCode(CommonType.TransactionCode.TRANSFER.getCode())
										.description("이체")
										.build()
						)
						.account(
								Account.builder()
										.accountId(2L)
										.accountNumber("614-79-603038")
										.codeBank(
												CodeBank.builder()
														.bankCode("003")
														.bankName("기업은행")
														.build()
										)
										.build()
						)
						.targetAccount(
								Account.builder()
										.accountId(3L)
										.accountNumber("125418063807897")
										.codeBank(
												CodeBank.builder()
														.bankCode("002")
														.bankName("산업은행")
														.build()
										)
										.build()
						)
						.amount(BigDecimal.valueOf(100000))
						.build(),
				AccountTransaction
						.builder()
						.transactionId(2L)
						.codeTransaction(
								CodeTransaction.builder()
										.transactionCode(CommonType.TransactionCode.WITHDRAW.getCode())
										.description("출금")
										.build()
						)
						.account(
								Account.builder()
										.accountId(2L)
										.accountNumber("614-79-603038")
										.codeBank(
												CodeBank.builder()
														.bankCode("003")
														.bankName("기업은행")
														.build()
										)
										.build()
						)
						.amount(BigDecimal.valueOf(50000))
						.build(),
				AccountTransaction
						.builder()
						.transactionId(1L)
						.codeTransaction(
								CodeTransaction.builder()
										.transactionCode(CommonType.TransactionCode.DEPOSIT.getCode())
										.description("입금")
										.build()
						)
						.account(
								Account.builder()
										.accountId(2L)
										.accountNumber("614-79-603038")
										.codeBank(
												CodeBank.builder()
														.bankCode("003")
														.bankName("기업은행")
														.build()
										)
										.build()
						)
						.amount(BigDecimal.valueOf(1000000))
						.build()
		);

		when(accountTransactionRepository.findAllByOrderByTransactionIdDesc()).thenReturn(accountTransactionList);

		// when
		BaseApiResponse<List<TransactionDto.Response>> response = transactionService.selectTransactionList();
		List<TransactionDto.Response> actualList = response.getData();

		List<TransactionDto.Response> expectedList = List.of(
				TransactionDto.Response.builder()
						.transactionId(3L)
						.transactionCode(CommonType.TransactionCode.TRANSFER.getCode())
						.description("이체")
						.accountId(2L)
						.accountNumber("614-79-603038")
						.bankName("기업은행")
						.targetAccountId(3L)
						.targetAccountNumber("125418063807897")
						.targetBankName("산업은행")
						.amount(BigDecimal.valueOf(100000))
						.build(),
				TransactionDto.Response.builder()
						.transactionId(2L)
						.transactionCode(CommonType.TransactionCode.WITHDRAW.getCode())
						.description("출금")
						.accountId(2L)
						.accountNumber("614-79-603038")
						.bankName("기업은행")
						.amount(BigDecimal.valueOf(50000))
						.build(),
				TransactionDto.Response.builder()
						.transactionId(1L)
						.transactionCode(CommonType.TransactionCode.DEPOSIT.getCode())
						.description("입금")
						.accountId(2L)
						.accountNumber("614-79-603038")
						.bankName("기업은행")
						.amount(BigDecimal.valueOf(1000000))
						.build()
		);

		// then
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());

		assertNotNull(actualList);
		assertEquals(expectedList.size(), actualList.size());

		for (int i = 0; i < expectedList.size(); i++) {
			TransactionDto.Response expected = expectedList.get(i);
			TransactionDto.Response actual = actualList.get(i);

			assertEquals(expected.getTransactionId(), actual.getTransactionId());
			assertEquals(expected.getTransactionCode(), actual.getTransactionCode());
			assertEquals(expected.getDescription(), actual.getDescription());
			assertEquals(expected.getAccountId(), actual.getAccountId());
			assertEquals(expected.getAccountId(), actual.getAccountId());
			assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
			assertEquals(expected.getBankName(), actual.getBankName());
			assertEquals(expected.getTargetAccountId(), actual.getTargetAccountId());
			assertEquals(expected.getTargetAccountNumber(), actual.getTargetAccountNumber());
			assertEquals(expected.getBankName(), actual.getBankName());
			assertTrue(expected.getAmount().compareTo(actual.getAmount()) == 0);
		}
	}

	/***
	 * 거래 조회 - 거래 연번 누락 test
	 */
	@Test
	public void selectTransaction_invalidParam() {
		// given
		Long transactionId = null;

		// when
		BaseApiResponse<TransactionDto.Response> response = transactionService.selectTransaction(transactionId);

		// then
		assertEquals(BaseApiResponse.error().getCode(), response.getCode());
		assertEquals("invalid request param", response.getMessage());
	}

	/***
	 * 거래 조회 - 정상 test
	 */
	@Test
	public void selectTransaction_valid() {
		// given
		AccountTransaction accountTransaction = AccountTransaction
				.builder()
				.transactionId(3L)
				.codeTransaction(
						CodeTransaction.builder()
								.transactionCode(CommonType.TransactionCode.TRANSFER.getCode())
								.description("이체")
								.build()
				)
				.account(
						Account.builder()
								.accountId(2L)
								.accountNumber("614-79-603038")
								.codeBank(
										CodeBank.builder()
												.bankCode("003")
												.bankName("기업은행")
												.build()
								)
								.build()
				)
				.targetAccount(
						Account.builder()
								.accountId(3L)
								.accountNumber("125418063807897")
								.codeBank(
										CodeBank.builder()
												.bankCode("002")
												.bankName("산업은행")
												.build()
								)
								.build()
				)
				.amount(BigDecimal.valueOf(100000))
				.build();

		when(accountTransactionRepository.findById(accountTransaction.getTransactionId()))
				.thenReturn(Optional.of(accountTransaction));

		// when
		BaseApiResponse<TransactionDto.Response> response =
				transactionService.selectTransaction(accountTransaction.getTransactionId());

		TransactionDto.Response actual = response.getData();

		TransactionDto.Response expected = TransactionDto.Response.builder()
				.transactionId(3L)
				.transactionCode(CommonType.TransactionCode.TRANSFER.getCode())
				.description("이체")
				.accountId(2L)
				.accountNumber("614-79-603038")
				.bankName("기업은행")
				.targetAccountId(3L)
				.targetAccountNumber("125418063807897")
				.targetBankName("산업은행")
				.amount(BigDecimal.valueOf(100000))
				.build();

		// then
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());

		assertNotNull(actual);
		assertEquals(expected.getTransactionId(), actual.getTransactionId());
		assertEquals(expected.getTransactionCode(), actual.getTransactionCode());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertEquals(expected.getTargetAccountId(), actual.getTargetAccountId());
		assertEquals(expected.getTargetAccountNumber(), actual.getTargetAccountNumber());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertTrue(expected.getAmount().compareTo(actual.getAmount()) == 0);
	}

	/***
	 * 거래 정보 유효성 - 공통 test
	 */
	@Test
	public void validateTransaction_common() {
		// Case 01: invalid account
		// - when
		String validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.DEPOSIT)
						.account(null)
						.build()
		);

		// - then
		assertEquals("invalid account id", validateMsg);

		// Case 02: invalid amount
		// - when
		validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.DEPOSIT)
						.account(new Account())
						.amount(BigDecimal.ZERO)
						.build()
		);

		// - then
		assertEquals("invalid amount", validateMsg);

		// Case 03: valid
		// - when
		validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.DEPOSIT)
						.account(new Account())
						.amount(BigDecimal.ONE)
						.build()
		);

		// - then
		assertNull(validateMsg);
	}

	/***
	 * 거래 정보 유효성 - 출금 test 
	 */
	@Test
	public void validateTransaction_withdraw() {
		// Case 01: exceed limit
		// - given
		// -- 잔액
		BigDecimal balance = BigDecimal.valueOf(1000000);
		// -- 출금 금액
		BigDecimal amount = BigDecimal.valueOf(50001);
		// -- 일 사용 금액
		BigDecimal dailyAmount = BigDecimal.valueOf(950000);
		// -- 이체 한도
		BigDecimal dailyLimit = BigDecimal.valueOf(1000000);

		// - when
		String validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.WITHDRAW)
						.account(new Account())
						.dailyLimit(dailyLimit)
						.dailyAmount(dailyAmount)
						.amount(amount)
						.build()
		);

		// - then
		assertEquals("exceeded limit", validateMsg);

		// Case 02: insufficient balance
		// - given
		// -- 잔액
		balance = BigDecimal.valueOf(50000);
		// -- 출금 금액
		amount = BigDecimal.valueOf(50001);
		// -- 일 사용 금액
		dailyAmount = BigDecimal.ZERO;
		// -- 이체 한도
		dailyLimit = BigDecimal.valueOf(1000000);

		// - when
		validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.WITHDRAW)
						.account(
								Account.builder()
										.balance(balance)	// 잔액
										.build()
						)
						.dailyLimit(dailyLimit)
						.dailyAmount(BigDecimal.ZERO)
						.amount(amount)
						.build()
		);

		// - then
		assertEquals("insufficient balance", validateMsg);

		// Case 03: valid
		// - given
		// -- 잔액
		balance = BigDecimal.valueOf(1000000);
		// -- 출금 금액
		amount = BigDecimal.valueOf(1000000);
		// -- 일 사용 금액
		dailyAmount = BigDecimal.ZERO;
		// -- 이체 한도
		dailyLimit = BigDecimal.valueOf(1000000);

		// - when
		validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.WITHDRAW)
						.account(
								Account.builder()
										.balance(balance)
										.build()
						)
						.dailyLimit(dailyLimit)
						.dailyAmount(dailyAmount)
						.amount(amount)
						.build()
		);

		// - then
		assertNull(validateMsg);
	}

	/***
	 * 거래 정보 유효성 - 이체 test
	 */
	@Test
	public void validateTransaction_transfer() {
		// given
		CodeTransaction codeTransaction = CodeTransaction.builder()
				.transactionCode(CommonType.TransactionCode.TRANSFER.getCode())
				.feePercent(BigDecimal.ONE)    // ex: 수수료 1%
				.build();

		when(codeService.getCodeTransactionMap())
				.thenReturn(Collections.singletonMap(codeTransaction.getTransactionCode(), codeTransaction));

		// Case 01: target account
		// - when
		String validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.TRANSFER)
						.account(new Account())
						.targetAccount(null)
						.amount(BigDecimal.ONE)
						.dailyAmount(BigDecimal.ZERO)
						.build()
		);

		// - then
		assertEquals("invalid target account id", validateMsg);

		// Case 02: exceed limit
		// - given
		// -- 잔액
		BigDecimal balance = BigDecimal.valueOf(100000);
		// -- 출금 금액
		BigDecimal amount = BigDecimal.valueOf(50001);
		// -- 일 사용 금액
		BigDecimal dailyAmount = BigDecimal.valueOf(2950000);
		// -- 이체 한도
		BigDecimal dailyLimit = BigDecimal.valueOf(3000000);

		// - when
		validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.TRANSFER)
						.account(new Account())
						.targetAccount(new Account())
						.dailyLimit(dailyLimit)
						.dailyAmount(dailyAmount)
						.amount(amount)
						.build()
		);

		// - then
		assertEquals("exceeded limit", validateMsg);

		// Case 03: insufficient balance
		// - given
		// -- 잔액
		balance = BigDecimal.valueOf(50000);
		// -- 출금 금액
		amount = BigDecimal.valueOf(50000);
		// -- 일 사용 금액
		dailyAmount = BigDecimal.ZERO;
		// -- 이체 한도
		dailyLimit = BigDecimal.valueOf(3000000);

		// - when
		validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.TRANSFER)
						.account(
								Account.builder()
										.balance(balance)
										.build()
						)
						.targetAccount(new Account())
						.dailyLimit(dailyLimit)
						.dailyAmount(dailyAmount)
						.amount(amount)
						.build()
		);

		// - then
		assertEquals("insufficient balance", validateMsg);

		// Case 04: valid
		// - given
		// -- 잔액
		balance = BigDecimal.valueOf(100000);
		// -- 출금 금액
		amount = BigDecimal.valueOf(90000);
		// -- 일 사용 금액
		dailyAmount = BigDecimal.valueOf(2900000);
		// -- 이체 한도
		dailyLimit = BigDecimal.valueOf(3000000);

		// - when
		validateMsg = transactionService.validateTransaction(
				TransactionDto.Transaction
						.builder()
						.transactionCode(CommonType.TransactionCode.TRANSFER)
						.account(
								Account.builder()
										.balance(balance)
										.build()
						)
						.targetAccount(new Account())
						.dailyLimit(dailyLimit)
						.dailyAmount(dailyAmount)
						.amount(amount)
						.build()
		);

		// - then
		assertNull(validateMsg);
	}

	/***
	 * 입금 - request body 누락 test
	 */
	@Test
	public void deposit_invalidRequestBody() {
		// given
		BaseApiRequest<TransactionDto.TransactionRequest> requestBody = new BaseApiRequest<>(null);

		// when
		BaseApiResponse<TransactionDto.Response> response = transactionService.deposit(requestBody);

		// then
		assertEquals(BaseApiResponse.error().getCode(), response.getCode());
		assertEquals("invalid request body", response.getMessage());
	}

	/***
	 * 입금 - 정상 test
	 */
	@Test
	public void deposit_valid() {
		// given
		// - 입금 금액
		BigDecimal amount = BigDecimal.valueOf(1000000);

		Account account = Account.builder()
				.accountId(1L)
				.accountNumber("614-79-603038")
				.balance(BigDecimal.ZERO)
				.codeBank(
						CodeBank.builder()
								.bankCode("003")
								.bankName("기업은행")
								.build()
				)
				.build();

		CodeTransaction codeTransaction = CodeTransaction.builder()
				.transactionCode(CommonType.TransactionCode.DEPOSIT.getCode())
				.description("입금")
				.build();

		AccountTransaction accountTransaction = AccountTransaction.builder()
				.transactionId(1L)
				.codeTransaction(codeTransaction)
				.account(account)
				.amount(amount)
				.build();

		TransactionDto.TransactionRequest request = TransactionDto.TransactionRequest
				.builder()
				.accountId(account.getAccountId())
				.amount(amount)
				.build();

		BaseApiRequest<TransactionDto.TransactionRequest> requestBody = new BaseApiRequest<>(request);

		when(accountRepository.findByAccountIdAndDelYn(request.getAccountId(), 0)).thenReturn(Optional.of(account));
		when(codeService.getCodeTransactionMap())
				.thenReturn(Collections.singletonMap(codeTransaction.getTransactionCode(), codeTransaction));
		when(accountTransactionRepository.save(any(AccountTransaction.class))).thenReturn(accountTransaction);
		when(accountRepository.save(any(Account.class))).thenReturn(account);
		when(accountTransactionRepository.findById(any(Long.class))).thenReturn(Optional.of(accountTransaction));

		// when
		BaseApiResponse<TransactionDto.Response> response = transactionService.deposit(requestBody);

		TransactionDto.Response actual = response.getData();

		TransactionDto.Response expected = TransactionDto.Response.builder()
				.transactionId(1L)
				.transactionCode(CommonType.TransactionCode.DEPOSIT.getCode())
				.description("입금")
				.accountId(1L)
				.accountNumber("614-79-603038")
				.bankName("기업은행")
				.amount(amount)
				.build();

		// then
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());

		assertNotNull(actual);
		assertEquals(expected.getTransactionId(), actual.getTransactionId());
		assertEquals(expected.getTransactionCode(), actual.getTransactionCode());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertEquals(expected.getTargetAccountId(), actual.getTargetAccountId());
		assertEquals(expected.getTargetAccountNumber(), actual.getTargetAccountNumber());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertTrue(expected.getAmount().compareTo(actual.getAmount()) == 0);
	}

	/***
	 * 출금 - request body 누락 test
	 */
	@Test
	public void withdraw_invalidRequestBody() {
		// given
		BaseApiRequest<TransactionDto.TransactionRequest> requestBody = new BaseApiRequest<>(null);

		// when
		BaseApiResponse<TransactionDto.Response> response = transactionService.withdraw(requestBody);

		// then
		assertEquals(BaseApiResponse.error().getCode(), response.getCode());
		assertEquals("invalid request body", response.getMessage());
	}
	
	/***
	 * 출금 - 정상 test
	 */
	@Test
	public void withdraw_valid() {
		// given
		// - 잔액
		BigDecimal balance = BigDecimal.valueOf(5000000);
		// - 출금 금액
		BigDecimal amount = BigDecimal.valueOf(1000000);
		// - 일 사용 금액
		BigDecimal dailyAmount = BigDecimal.ZERO;
		// - 이체 한도
		BigDecimal dailyLimit = BigDecimal.valueOf(1000000);

		Account account = Account.builder()
				.accountId(1L)
				.accountNumber("614-79-603038")
				.balance(balance)
				.codeBank(
						CodeBank.builder()
								.bankCode("003")
								.bankName("기업은행")
								.build()
				)
				.accountLimitList(
						List.of(
								AccountLimit.builder()
										.accountId(1L)
										.transactionCode(CommonType.TransactionCode.WITHDRAW.getCode())
										.dailyAmount(dailyAmount)
										.build()
						)
				)
				.build();

		CodeTransaction codeTransaction = CodeTransaction.builder()
				.transactionCode(CommonType.TransactionCode.WITHDRAW.getCode())
				.description("출금")
				.dailyLimit(dailyLimit)
				.build();

		AccountTransaction accountTransaction = AccountTransaction.builder()
				.transactionId(1L)
				.codeTransaction(codeTransaction)
				.account(account)
				.amount(amount)
				.build();

		AccountLimit accountLimit = AccountLimit.builder()
				.accountId(1L)
				.transactionCode(CommonType.TransactionCode.WITHDRAW.getCode())
				.dailyAmount(dailyAmount)
				.build();

		TransactionDto.TransactionRequest request = TransactionDto.TransactionRequest
				.builder()
				.accountId(account.getAccountId())
				.amount(amount)
				.build();

		BaseApiRequest<TransactionDto.TransactionRequest> requestBody = new BaseApiRequest<>(request);

		when(accountRepository.findByAccountIdAndDelYn(request.getAccountId(), 0)).thenReturn(Optional.of(account));
		when(codeService.getCodeTransactionMap())
				.thenReturn(Collections.singletonMap(codeTransaction.getTransactionCode(), codeTransaction));
		when(accountTransactionRepository.save(any(AccountTransaction.class))).thenReturn(accountTransaction);
		when(accountLimitRepository.save(any(AccountLimit.class))).thenReturn(accountLimit);
		when(accountTransactionRepository.findById(any(Long.class))).thenReturn(Optional.of(accountTransaction));

		// when
		BaseApiResponse<TransactionDto.Response> response = transactionService.withdraw(requestBody);

		TransactionDto.Response actual = response.getData();

		TransactionDto.Response expected = TransactionDto.Response.builder()
				.transactionId(1L)
				.transactionCode(CommonType.TransactionCode.WITHDRAW.getCode())
				.description("출금")
				.accountId(1L)
				.accountNumber("614-79-603038")
				.bankName("기업은행")
				.amount(amount)
				.build();

		// then
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());

		assertNotNull(actual);
		assertEquals(expected.getTransactionId(), actual.getTransactionId());
		assertEquals(expected.getTransactionCode(), actual.getTransactionCode());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertEquals(expected.getTargetAccountId(), actual.getTargetAccountId());
		assertEquals(expected.getTargetAccountNumber(), actual.getTargetAccountNumber());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertTrue(expected.getAmount().compareTo(actual.getAmount()) == 0);
	}

	/***
	 * 이체 - request body 누락 test
	 */
	@Test
	public void transfer_invalidRequestBody() {
		// given
		BaseApiRequest<TransactionDto.TransferRequest> requestBody = new BaseApiRequest<>(null);

		// when
		BaseApiResponse<TransactionDto.Response> response = transactionService.transfer(requestBody);

		// then
		assertEquals(BaseApiResponse.error().getCode(), response.getCode());
		assertEquals("invalid request body", response.getMessage());
	}
	
	/***
	 * 이체 - 정상 test
	 */
	@Test
	public void transfer_valid() {
		// given
		// - 잔액
		BigDecimal balance = BigDecimal.valueOf(3100000);
		// - 이체 금액
		BigDecimal amount = BigDecimal.valueOf(3000000);
		// - 일 사용 금액
		BigDecimal dailyAmount = BigDecimal.ZERO;
		// - 이체 한도
		BigDecimal dailyLimit = BigDecimal.valueOf(3000000);

		Account account = Account.builder()
				.accountId(1L)
				.accountNumber("614-79-603038")
				.balance(balance)
				.codeBank(
						CodeBank.builder()
								.bankCode("003")
								.bankName("기업은행")
								.build()
				)
				.accountLimitList(
						List.of(
								AccountLimit.builder()
										.accountId(1L)
										.transactionCode(CommonType.TransactionCode.TRANSFER.getCode())
										.dailyAmount(dailyAmount)
										.build()
						)
				)
				.build();

		Account targetAccount = Account.builder()
				.accountId(2L)
				.accountNumber("125418063807897")
				.codeBank(
						CodeBank.builder()
								.bankCode("002")
								.bankName("산업은행")
								.build()
				)
				.build();

		CodeTransaction codeTransaction = CodeTransaction.builder()
				.transactionCode(CommonType.TransactionCode.TRANSFER.getCode())
				.description("이체")
				.dailyLimit(dailyLimit)
				.feePercent(BigDecimal.ONE)    // 수수료 1%
				.build();

		AccountTransaction accountTransaction = AccountTransaction.builder()
				.transactionId(1L)
				.codeTransaction(codeTransaction)
				.account(account)
				.targetAccount(targetAccount)
				.amount(amount)
				.build();

		AccountLimit accountLimit = AccountLimit.builder()
				.accountId(account.getAccountId())
				.transactionCode(codeTransaction.getTransactionCode())
				.dailyAmount(dailyAmount)
				.build();

		TransactionFee transactionFee = TransactionFee.builder()
				.transactionId(accountTransaction.getTransactionId())
				.fee(BigDecimal.ZERO)
				.build();

		TransactionDto.TransferRequest request = TransactionDto.TransferRequest
				.builder()
				.accountId(account.getAccountId())
				.targetAccountId(targetAccount.getAccountId())
				.amount(amount)
				.build();

		BaseApiRequest<TransactionDto.TransferRequest> requestBody = new BaseApiRequest<>(request);

		when(accountRepository.findByAccountIdAndDelYn(account.getAccountId(), 0))
				.thenReturn(Optional.of(account));
		when(accountRepository.findByAccountIdAndDelYn(targetAccount.getAccountId(), 0))
				.thenReturn(Optional.of(targetAccount));
		when(codeService.getCodeTransactionMap())
				.thenReturn(Collections.singletonMap(codeTransaction.getTransactionCode(), codeTransaction));
		when(accountTransactionRepository.save(any(AccountTransaction.class))).thenReturn(accountTransaction);
		when(transactionFeeRepository.save(any(TransactionFee.class))).thenReturn(transactionFee);
		when(accountLimitRepository.save(any(AccountLimit.class))).thenReturn(accountLimit);
		when(accountTransactionRepository.findById(any(Long.class))).thenReturn(Optional.of(accountTransaction));

		// when
		BaseApiResponse<TransactionDto.Response> response = transactionService.transfer(requestBody);

		TransactionDto.Response actual = response.getData();

		TransactionDto.Response expected = TransactionDto.Response.builder()
				.transactionId(1L)
				.transactionCode(CommonType.TransactionCode.TRANSFER.getCode())
				.description("이체")
				.accountId(1L)
				.accountNumber("614-79-603038")
				.bankName("기업은행")
				.targetAccountId(2L)
				.targetAccountNumber("125418063807897")
				.targetBankName("산업은행")
				.amount(amount)
				.build();

		// then
		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());

		assertNotNull(actual);
		assertEquals(expected.getTransactionId(), actual.getTransactionId());
		assertEquals(expected.getTransactionCode(), actual.getTransactionCode());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getAccountId(), actual.getAccountId());
		assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertEquals(expected.getTargetAccountId(), actual.getTargetAccountId());
		assertEquals(expected.getTargetAccountNumber(), actual.getTargetAccountNumber());
		assertEquals(expected.getBankName(), actual.getBankName());
		assertTrue(expected.getAmount().compareTo(actual.getAmount()) == 0);
	}
}