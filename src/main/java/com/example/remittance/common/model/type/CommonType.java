package com.example.remittance.common.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 공통 유형
 * @author evan.m.kim
 */
public class CommonType {
	/***
	 * 거래 유형 - 코드
	 */
	@Getter
	@RequiredArgsConstructor
	public enum TransactionCode {
		@Schema(description = "입금")
		DEPOSIT("00"),

		@Schema(description = "출금")
		WITHDRAW("01"),

		@Schema(description = "이체")
		TRANSFER("02");


		private final String code;
	}
}
