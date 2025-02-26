package com.example.remittance.account.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.example.remittance.account.model.AccountDto;
import com.example.remittance.common.model.base.BaseApiRequest;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.common.model.entity.Account;
import com.example.remittance.common.model.entity.AccountLimit;
import com.example.remittance.common.model.entity.CodeBank;
import com.example.remittance.common.repository.AccountLimitRepository;
import com.example.remittance.common.repository.AccountRepository;
import com.example.remittance.common.repository.CodeTransactionRepository;
import com.example.remittance.common.service.CodeService;
import com.example.remittance.common.util.RandomStringUtil;

/**
 * 계좌 정보 service
 * @author evan.m.kim
 */

@Service
@RequiredArgsConstructor
public class AccountService {
	private final CodeService codeService;

	private final AccountRepository accountRepository;
	private final AccountLimitRepository accountLimitRepository;
	private final CodeTransactionRepository codeTransactionRepository;

	/***
	 * 계좌 리스트 조회
	 * @return 계좌 정보 DTO list
	 */
	public BaseApiResponse<List<AccountDto>> selectAccountList() {
		List<Account> srcList = accountRepository.findAllByDelYn(0);

		List<AccountDto> accountList = new ArrayList<>();

		srcList.forEach(src -> {
			Optional.ofNullable(this.bindDto(src))
					.ifPresent(accountList::add);
		});

		return BaseApiResponse.ok(accountList);
	}

	/***
	 * 계좌 조회
	 * @param accountId 계좌 연번
	 * @return 계좌 정보 DTO
	 */
	@Transactional
	public BaseApiResponse<AccountDto> selectAccount(Long accountId) {
		if (accountId == null) {
			return BaseApiResponse.error("invalid request param");
		}

		return BaseApiResponse.ok(
			this.bindDto(accountRepository.findByAccountIdAndDelYn(accountId, 0).orElse(null))
		);
	}

	/***
	 * 계좌 등록
	 * - 계좌번호는 은행별 계좌 형식에 따라 랜덤 부여합니다.
	 * @param requestBody
	 * @return 계좌 정보 DTO
	 */
	@Transactional
	public BaseApiResponse<AccountDto> insertAccount(
			BaseApiRequest<AccountDto> requestBody) {
		AccountDto dest = requestBody.getData();

		// validate request
		if (dest == null) {
			return BaseApiResponse.error("invalid request body");
		}

		CodeBank srcCb = codeService.getCodeBankMap().get(dest.getBankCode());

		if (srcCb == null) {
			return BaseApiResponse.error("invalid bank code");
		}

		// generate account number
		String accountNumber = null;

		do {
			accountNumber = RandomStringUtil.generateAccountNumber(srcCb.getAccountFormat());
		} while (accountRepository.existsByCodeBankAndAccountNumber(srcCb, accountNumber));

		// insert db
		// - account
		Account account = accountRepository.save(
				Account.builder()
						.codeBank(srcCb)
						.accountNumber(accountNumber)
						.build()
		);

		// - account limit
		List<AccountLimit> accountLimitList = new ArrayList<>();

		codeTransactionRepository.findAll()
				.forEach(srcCt -> {
					accountLimitList.add(
							AccountLimit.builder()
									.accountId(account.getAccountId())
									.transactionCode(srcCt.getTransactionCode())
									.build()
					);
				});

		accountLimitRepository.saveAll(accountLimitList);

		return this.selectAccount(account.getAccountId());
	}

	/***
	 * 계좌 삭제
	 * - 삭제 플래그를 설정합니다.
	 * @param accountId 계좌 연번
	 * @return void
	 */
	@Transactional
	public BaseApiResponse<Void> deleteAccount(Long accountId) {
		if (accountId == null) {
			return BaseApiResponse.error("invalid request param");
		}

		Account account = accountRepository.findByAccountIdAndDelYn(accountId, 0).orElse(null);

		if (account == null) {
			return BaseApiResponse.error("not found account");
		}

		account.setDelYn(1);
		accountRepository.save(account);

		return BaseApiResponse.ok();
	}

	/**
	 * 계좌 entity를 DTO로 변환
	 * @param src 계좌 entity
	 * @return 계좌 정보 DTO
	 */
	private AccountDto bindDto(Account src) {
		// validate
		if (src == null || src.getCodeBank() == null) {
			return null;
		}

		return AccountDto.builder()
				.accountId(src.getAccountId())
				.bankCode(src.getCodeBank().getBankCode())
				.bankName(src.getCodeBank().getBankName())
				.accountNumber(src.getAccountNumber())
				.balance(src.getBalance())
				.build();
	}
}
