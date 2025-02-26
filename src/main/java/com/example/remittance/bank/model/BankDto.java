package com.example.remittance.bank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 은행 정보 DTO
 * @author evan.m.kim
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BankDto {
	@Schema(description = "은행 코드")
	private String bankCode;

	@Schema(description = "은행 명")
	private String bankName;
}
