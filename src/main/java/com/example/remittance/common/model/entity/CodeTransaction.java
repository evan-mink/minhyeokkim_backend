package com.example.remittance.common.model.entity;

import java.math.BigDecimal;

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
 * 코드-거래 유형 entity
 * @author evan.m.kim
 */

@Entity
@Table(name = "code_transaction")
@Schema

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CodeTransaction extends BaseEntity {
	@Id
	@Column(name = "transaction_code")
	@Schema(description = "거래 유형 코드")
	private String transactionCode;

	@Column(name = "description")
	@Schema(description = "설명")
	private String description;

	@Column(name = "daily_limit", precision = 15, scale = 2)
	@Schema(description = "일 한도")
	private BigDecimal dailyLimit;

	@Column(name = "fee_percent", precision = 5, scale = 2)
	@Schema(description = "수수료 비율")
	private BigDecimal feePercent;
}
