package com.example.remittance.transaction.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.example.remittance.common.model.base.BaseApiRequest;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.common.model.entity.Account;
import com.example.remittance.common.model.entity.AccountLimit;
import com.example.remittance.common.model.entity.AccountTransaction;
import com.example.remittance.common.model.entity.CodeTransaction;
import com.example.remittance.common.model.entity.TransactionFee;
import com.example.remittance.common.model.type.CommonType;
import com.example.remittance.common.repository.AccountLimitRepository;
import com.example.remittance.common.repository.AccountRepository;
import com.example.remittance.common.repository.AccountTransactionRepository;
import com.example.remittance.common.repository.TransactionFeeRepository;
import com.example.remittance.common.service.CodeService;
import com.example.remittance.transaction.model.TransactionDto;

/**
 * 거래 정보 service
 * @author evan.m.kim
 */

@Service
@RequiredArgsConstructor
public class TransactionService {
	private final CodeService codeService;

	private final AccountRepository accountRepository;
	private final AccountTransactionRepository accountTransactionRepository;
	private final AccountLimitRepository accountLimitRepository;
	private final TransactionFeeRepository transactionFeeRepository;

	/***
	 * 거래 리스트 조회
	 * - 거래 내역 역순으로 조회됩니다.
	 * @return 거래 정보 DTO list
	 */
	public BaseApiResponse<List<TransactionDto.Response>> selectTransactionList() {
		List<AccountTransaction> srcList = accountTransactionRepository.findAllByOrderByTransactionIdDesc();

		List<TransactionDto.Response> transactionList = new ArrayList<>();

		srcList.forEach(src -> {
			Optional.ofNullable(this.bindDto(src))
					.ifPresent(transactionList::add);
		});

		return BaseApiResponse.ok(transactionList);
	}

	/***
	 * 거래 조회
	 * @param transactionId 거래 연번
	 * @return 거래 DTO
	 */
	public BaseApiResponse<TransactionDto.Response> selectTransaction(Long transactionId) {
		// validate request
		if (transactionId == null) {
			return BaseApiResponse.error("invalid request param");
		}

		AccountTransaction src = accountTransactionRepository.findById(transactionId).orElse(null);

		return BaseApiResponse.ok(this.bindDto(src));
	}

	/***
	 * 입금
	 * @param requestBody
	 * @return 거래 DTO
	 */
	@Transactional
	public BaseApiResponse<TransactionDto.Response> deposit(
			BaseApiRequest<TransactionDto.TransactionRequest> requestBody) {
		TransactionDto.TransactionRequest dest = requestBody.getData();

		// validate
		// - request
		if (isInvalidRequestBody(dest)) {
			return BaseApiResponse.error("invalid request body");
		}

		// - transaction
		CommonType.TransactionCode transactionCode = CommonType.TransactionCode.DEPOSIT;

		Account account = accountRepository.findByAccountIdAndDelYn(dest.getAccountId(), 0).orElse(null);
		BigDecimal amount = dest.getAmount();

		TransactionDto.Transaction transaction = TransactionDto.Transaction.builder()
				.transactionCode(transactionCode)
				.account(account)
				.amount(amount)
				.build();

		String validateMsg = this.validateTransaction(transaction);

		if (validateMsg != null) {
			return BaseApiResponse.error(validateMsg);
		}

		// process
		Long transactionId = this.processTransaction(transaction);

		return this.selectTransaction(transactionId);
	}

	/***
	 * 출금
	 * - 계좌 금액을 출금합니다.
	 * - 잔액을 초과할 수 없습니다.
	 * - 계좌 별 일일 한도를 초과할 수 없습니다. (초기 값: 1,000,000)
	 *
	 * @param requestBody
	 * @return 거래 DTO
	 */
	@Transactional
	public BaseApiResponse<TransactionDto.Response> withdraw(
			BaseApiRequest<TransactionDto.TransactionRequest> requestBody) {
		TransactionDto.TransactionRequest dest = requestBody.getData();

		// validate
		// - request
		if (isInvalidRequestBody(dest)) {
			return BaseApiResponse.error("invalid request body");
		}

		// - transaction
		CommonType.TransactionCode transactionCode = CommonType.TransactionCode.WITHDRAW;

		Account account = accountRepository.findByAccountIdAndDelYn(dest.getAccountId(), 0).orElse(null);
		BigDecimal amount = dest.getAmount();
		BigDecimal dailyAmount = this.getDailyAmount(account, transactionCode.getCode());
		BigDecimal dailyLimit = this.getDailyLimit(transactionCode.getCode());

		TransactionDto.Transaction transaction = TransactionDto.Transaction.builder()
				.transactionCode(transactionCode)
				.account(account)
				.amount(amount)
				.dailyAmount(dailyAmount)
				.dailyLimit(dailyLimit)
				.build();

		String validateMsg = this.validateTransaction(transaction);

		if (validateMsg != null) {
			return BaseApiResponse.error(validateMsg);
		}

		// process
		Long transactionId = this.processTransaction(transaction);

		return this.selectTransaction(transactionId);
	}

	/***
	 * 이체
	 * - 계좌 금액을 이체합니다.
	 * - 거래 금액에 수수료를 부과합니다. (초기 값: 거래 금액의 1%)
	 * - 잔액을 초과할 수 없습니다.
	 * - 계좌별 일일 한도를 초과할 수 없습니다. (초기 값: 3,000,000)
	 *
	 * @param requestBody
	 * @return 거래 DTO
	 */
	@Transactional
	public BaseApiResponse<TransactionDto.Response> transfer(
			BaseApiRequest<TransactionDto.TransferRequest> requestBody) {
		TransactionDto.TransferRequest dest = requestBody.getData();

		// validate
		// - request
		if (isInvalidRequestBody(dest)) {
			return BaseApiResponse.error("invalid request body");
		}

		// - transaction
		CommonType.TransactionCode transactionCode = CommonType.TransactionCode.TRANSFER;

		Account account = accountRepository.findByAccountIdAndDelYn(dest.getAccountId(), 0).orElse(null);
		Account targetAccount = accountRepository.findByAccountIdAndDelYn(dest.getTargetAccountId(), 0).orElse(null);
		BigDecimal amount = dest.getAmount();
		BigDecimal dailyAmount = this.getDailyAmount(account, transactionCode.getCode());
		BigDecimal dailyLimit = this.getDailyLimit(transactionCode.getCode());

		TransactionDto.Transaction transaction = TransactionDto.Transaction.builder()
				.transactionCode(transactionCode)
				.account(account)
				.targetAccount(targetAccount)
				.amount(amount)
				.dailyAmount(dailyAmount)
				.dailyLimit(dailyLimit)
				.build();

		String validateMsg = this.validateTransaction(transaction);

		if (validateMsg != null) {
			return BaseApiResponse.error(validateMsg);
		}

		// process
		Long transactionId = this.processTransaction(transaction);

		return this.selectTransaction(transactionId);
	}

	/***
	 * 계좌-거래 entity를 DTO로 변환
	 * @param src 계좌-거래 entity
	 * @return 거래 정보 - 응답 DTO
	 */
	private TransactionDto.Response bindDto(AccountTransaction src) {
		// validate
		if ((src == null)
				|| (src.getCodeTransaction() == null)
				|| (src.getAccount() == null)) {
			return null;
		}

		String transactionCode = src.getCodeTransaction().getTransactionCode();

		// - '이체' 유형인 경우 대상 계좌 정보 validate
		if ((transactionCode.equals(CommonType.TransactionCode.TRANSFER.getCode()))
				&& (src.getTargetAccount() == null)) {
			return null;
		}

		// set dto
		TransactionDto.Response dto = TransactionDto.Response.builder()
				.transactionId(src.getTransactionId())
				.transactionCode(transactionCode)
				.accountId(src.getAccount().getAccountId())
				.accountNumber(src.getAccount().getAccountNumber())
				.bankName(src.getAccount().getCodeBank().getBankName())
				.description(src.getCodeTransaction().getDescription())
				.amount(src.getAmount())
				.regDate(src.getRegDate())
				.build();

		// - '이체' 유형인 경우 대상 계좌 정보 포함
		if (transactionCode.equals(CommonType.TransactionCode.TRANSFER.getCode())) {
			dto.setTargetAccountId(src.getTargetAccount().getAccountId());
			dto.setTargetAccountNumber(src.getTargetAccount().getAccountNumber());
			dto.setTargetBankName(src.getTargetAccount().getCodeBank().getBankName());
		}

		return dto;
	}

	/***
	 * request body 유효성 검사
	 * - 거래 유형에 따라 request body를 검증합니다.
	 * 
	 * @param request 거래 정보 - 요청 generic class
	 * @param <T> 거래 정보 - 요청 DTO 상속 class
	 * @return 유효하지 않은 경우 true
	 */
	private <T extends TransactionDto.TransactionRequest> boolean isInvalidRequestBody(T request) {
		// 공통 유형 검증
		if (request == null || request.getAccountId() == null) {
			return true;
		}

		// '이체' 유형 request 검증
		if (request instanceof TransactionDto.TransferRequest transferRequest) {
			return transferRequest.getTargetAccountId() == null;
		}

		return false;
	}

	/***
	 * 거래 정보 유효성 검사
	 * - 거래 유형에 따라 검증합니다.
	 * 
	 * @param transaction 거래 정보 - 거래 DTO
	 * @return 유효성 검증 메시지 문자열
	 */
	public String validateTransaction(TransactionDto.Transaction transaction) {
		// 공통 검증
		// - 계좌
		if (transaction.getAccount() == null) {
			return "invalid account id";
		}

		// - 금액
		if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return "invalid amount";
		}

		// 출금, 이체 유형 검증
		CommonType.TransactionCode transactionCode = transaction.getTransactionCode();

		if (!transactionCode.equals(CommonType.TransactionCode.DEPOSIT)) {
			BigDecimal amount = transaction.getAmount();
			BigDecimal dailyAmount = transaction.getDailyAmount().add(amount);

			// - 이체 유형
			if (transactionCode.equals(CommonType.TransactionCode.TRANSFER)) {
				// -- 대상 계좌
				if (transaction.getTargetAccount() == null) {
					return "invalid target account id";
				}

				// -- 수수료 포함 금액 설정
				BigDecimal feePercent = codeService.getCodeTransactionMap()
						.get(transactionCode.getCode())
						.getFeePercent();

				amount = amount.add(this.calcFeeAmount(amount, feePercent));
			}

			// - 한도 초과
			if (transaction.getDailyLimit().compareTo(dailyAmount) < 0) {
				return "exceeded limit";
			}

			// - 잔액 부족
			if (transaction.getAccount().getBalance().compareTo(amount) < 0) {
				return "insufficient balance";
			}
		}

		return null;
	}

	/***
	 * 수수료 금액 계산
	 * @param amount 금액
	 * @param feePercent 수수료율
	 * @return 수수료 금액
	 */
	private BigDecimal calcFeeAmount(BigDecimal amount, BigDecimal feePercent) {
		if ((amount != null)
				&& (feePercent != null)
				&& (feePercent.compareTo(BigDecimal.ZERO) > 0)) {
			return amount.multiply(feePercent).divide(BigDecimal.valueOf(100));
		}

		return BigDecimal.ZERO;
	}

	/***
	 * 계좌, 거래 유형 별 일 사용 금액 조회 
	 * @param account 계좌 entity
	 * @param transactionCode 거래 유형 코드 문자열
	 * @return 일 사용 금액
	 */
	private BigDecimal getDailyAmount(Account account, String transactionCode) {
		if (account != null) {
			return account.getAccountLimitList()
					.stream()
					.filter(src -> src.getTransactionCode().equals(transactionCode))
					.findFirst()
					.map(AccountLimit::getDailyAmount)
					.orElse(null);
		}

		return null;
	}

	/***
	 * 거래 유형 별 한도 금액 조회
	 * @param transactionCode 거래 유형 코드 문자열
	 * @return 한도 금액
	 */
	private BigDecimal getDailyLimit(String transactionCode) {
		return codeService.getCodeTransactionMap()
				.get(transactionCode)
				.getDailyLimit();
	}

	/***
	 * 거래 처리
	 * @param transaction 거래 정보 - 거래 DTO
	 * @return 거래 연번
	 */
	@Transactional
	protected Long processTransaction(TransactionDto.Transaction transaction) {
		CodeTransaction codeTransaction = codeService.getCodeTransactionMap()
				.get(transaction.getTransactionCode().getCode());

		AccountTransaction accountTransaction = null;

		switch (transaction.getTransactionCode()) {
			case DEPOSIT: {
				// insert db
				// - account_transaction
				accountTransaction = accountTransactionRepository.save(
						AccountTransaction.builder()
								.codeTransaction(codeTransaction)
								.account(transaction.getAccount())
								.amount(transaction.getAmount())
								.build()
				);

				// update_db
				// - account
				Account account = transaction.getAccount();
				account.setBalance(account.getBalance().add(transaction.getAmount()));
				accountRepository.save(account);
				break;
			}
			case WITHDRAW: {
				// insert db
				// - account_transaction
				accountTransaction = accountTransactionRepository.save(
						AccountTransaction.builder()
								.codeTransaction(codeTransaction)
								.account(transaction.getAccount())
								.amount(transaction.getAmount())
								.build()
				);

				// update db
				// - account
				Account account = transaction.getAccount();
				account.setBalance(account.getBalance().subtract(transaction.getAmount()));

				// - account_limit
				AccountLimit accountLimit = account.getAccountLimitList()
						.stream()
						.filter(src -> src.getTransactionCode().equals(codeTransaction.getTransactionCode()))
						.findFirst()
						.orElse(null);

				if (accountLimit != null) {
					accountLimit.setDailyAmount(transaction.getDailyAmount().add(transaction.getAmount()));
					accountLimitRepository.save(accountLimit);
				}
				break;
			}
			case TRANSFER: {
				// 수수료
				BigDecimal feeAmount = this.calcFeeAmount(transaction.getAmount(), codeTransaction.getFeePercent());

				// insert db
				// - account_transaction
				accountTransaction = accountTransactionRepository.save(
						AccountTransaction.builder()
								.codeTransaction(codeTransaction)
								.account(transaction.getAccount())
								.targetAccount(transaction.getTargetAccount())
								.amount(transaction.getAmount())
								.build()
				);

				// - transaction_fee
				transactionFeeRepository.save(
						TransactionFee.builder()
								.transactionId(accountTransaction.getTransactionId())
								.fee(feeAmount)
								.build()
				);

				// update_db
				// - account
				Account account = transaction.getAccount();
				account.setBalance(
						account.getBalance()
								.subtract(transaction.getAmount())
								.subtract(feeAmount)
				);

				Account targetAccount = transaction.getTargetAccount();
				targetAccount.setBalance(targetAccount.getBalance().add(transaction.getAmount()));

				accountRepository.saveAll(List.of(account, targetAccount));

				// - account_limit
				AccountLimit accountLimit = account.getAccountLimitList()
						.stream()
						.filter(src -> src.getTransactionCode().equals(codeTransaction.getTransactionCode()))
						.findFirst()
						.orElse(null);

				if (accountLimit != null) {
					accountLimit.setDailyAmount(transaction.getDailyAmount().add(transaction.getAmount()));
					accountLimitRepository.save(accountLimit);
				}

				break;
			}
		}

		return Optional.ofNullable(accountTransaction)
				.map(AccountTransaction::getTransactionId)
				.orElse(null);
	}
}
