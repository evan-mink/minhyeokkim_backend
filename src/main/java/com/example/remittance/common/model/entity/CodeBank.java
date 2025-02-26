package com.example.remittance.common.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.example.remittance.common.model.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 코드-은행 entity
 * @author evan.m.kim
 */

@Entity
@Table(name = "code_bank")
@Schema

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CodeBank extends BaseEntity {
	@Id
	@Column(name = "bank_code")
	@Schema(description = "은행 코드")
	private String bankCode;

	@Column(name = "bank_name")
	@Schema(description = "은행 이름")
	private String bankName;

	@Column(name = "account_format")
	@Schema(description = "계좌번호 형식")
	private String accountFormat;
}
