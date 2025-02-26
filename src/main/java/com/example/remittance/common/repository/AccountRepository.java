package com.example.remittance.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.example.remittance.common.model.entity.Account;
import com.example.remittance.common.model.entity.CodeBank;

import jakarta.persistence.LockModeType;

/**
 * 계좌 repository
 * @author evan.m.kim
 */

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findAllByDelYn(Integer delYn);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Account> findByAccountIdAndDelYn(Long accountId, Integer delYn);

	boolean existsByCodeBankAndAccountNumber(CodeBank codeBank, String accountNumber);
}
