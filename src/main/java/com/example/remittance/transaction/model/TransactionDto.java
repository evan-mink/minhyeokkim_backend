package com.example.remittance.transaction.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.example.remittance.common.model.entity.Account;
import com.example.remittance.common.model.type.CommonType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author evan.m.kim
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class TransactionDto {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class Response {
		@Schema(description = "거래 연번")
		private Long transactionId;

		@Schema(description = "거래 유형 코드")
		private String transactionCode;

		@Schema(description = "거래 유형 설명")
		private String description;

		@Schema(description = "계좌 연번")
		private Long accountId;

		@Schema(description = "계좌 번호")
		private String accountNumber;

		@Schema(description = "은행 이름")
		private String bankName;

		@Schema(description = "대상 계좌 연번")
		private Long targetAccountId;

		@Schema(description = "대상 계좌 번호")
		private String targetAccountNumber;

		@Schema(description = "대상 은행 이름")
		private String targetBankName;

		@Schema(description = "금액")
		private BigDecimal amount;

		@Schema(description = "등록일자")
		private String regDate;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class TransactionRequest {
		@Schema(description = "계좌 연번")
		private Long accountId;

		@Schema(description = "금액")
		private BigDecimal amount;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class TransferRequest extends TransactionRequest {
		@Schema(description = "대상 계좌 연번")
		private Long targetAccountId;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@Builder
	public static class Transaction {
		@Schema(description = "거래 유형")
		private CommonType.TransactionCode transactionCode;

		@Schema(description = "계좌")
		private Account account;

		@Schema(description = "대상 계좌")
		private Account targetAccount;

		@Schema(description = "금액")
		private BigDecimal amount;

		@Schema(description = "일 사용 금액")
		private BigDecimal dailyAmount;

		@Schema(description = "일 한도 금액")
		private BigDecimal dailyLimit;
	}
}
