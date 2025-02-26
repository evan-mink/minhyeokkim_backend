package com.example.remittance.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.remittance.common.model.entity.AccountTransaction;

/**
 * 계좌-거래 repository
 * @author evan.m.kim
 */

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
	List<AccountTransaction> findAllByOrderByTransactionIdDesc();
}
