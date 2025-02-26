package com.example.remittance.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.remittance.common.model.entity.AccountLimit;
import com.example.remittance.common.model.entity.id.AccountLimitId;

/**
 * 계좌-한도 repository
 * @author evan.m.kim
 */

@Repository
public interface AccountLimitRepository extends JpaRepository<AccountLimit, AccountLimitId> {
	@Modifying
	@Transactional
	@Query("update AccountLimit al set al.dailyAmount = 0")
	void resetDailyAmount();
}
