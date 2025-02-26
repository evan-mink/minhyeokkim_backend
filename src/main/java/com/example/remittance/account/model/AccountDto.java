package com.example.remittance.account.model;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 계좌 정보 DTO
 * @author evan.m.kim
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AccountDto {
	@Schema(description = "계좌 연번",
			accessMode = Schema.AccessMode.READ_ONLY)
	private Long accountId;

	@Schema(description = "은행 코드")
	private String bankCode;

	@Schema(description = "은행 이름",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String bankName;

	@Schema(description = "계좌 번호",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String accountNumber;

	@Schema(description = "잔액",
			accessMode = Schema.AccessMode.READ_ONLY)
	private BigDecimal balance;
}
