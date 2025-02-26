package com.example.remittance.common.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.remittance.common.repository.AccountLimitRepository;

import lombok.RequiredArgsConstructor;

/**
 * batch 속성 component
 * @author evan.m.kim
 */
@Component
@RequiredArgsConstructor
public class BatchComponent {
	private final AccountLimitRepository accountLimitRepository;

	/***
	 * 매일 정각 계좌 별 한도를 0으로 초기화합니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void resetDailyAmount() {
		accountLimitRepository.resetDailyAmount();
	}
}
