package com.example.remittance.integration.common.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.remittance.common.model.entity.AccountLimit;
import com.example.remittance.common.repository.AccountLimitRepository;

/***
 * 계좌-한도 repository test
 */
@SpringBootTest(properties = "spring.profiles.active:local")
@Transactional
class AccountLimitRepositoryTest {
	@Autowired
	private AccountLimitRepository accountLimitRepository;

	/***
	 * 계좌 별 일 사용량 초기화
	 */
	@Test
	void resetDailyAmount() {
		// When
		accountLimitRepository.resetDailyAmount();

		// Then
		BigDecimal totalDailyAmount = accountLimitRepository.findAll()
				.stream()
				.map(AccountLimit::getDailyAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		assertEquals(0, totalDailyAmount.compareTo(BigDecimal.ZERO));
	}

}