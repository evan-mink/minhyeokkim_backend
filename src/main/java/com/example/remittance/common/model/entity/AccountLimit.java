package com.example.remittance.common.model.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.example.remittance.common.model.base.BaseEntity;
import com.example.remittance.common.model.entity.id.AccountLimitId;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * 계좌-한도 entity
 * @author evan.m.kim
 */

@Entity
@Table(name = "account_limit")
@IdClass(AccountLimitId.class)
@Schema

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AccountLimit extends BaseEntity {
	@Id
	@Column(name = "account_id")
	@Schema(description = "계좌 연번")
	private Long accountId;

	@Id
	@Column(name = "transaction_code")
	@Schema(description = "거래 유형 코드")
	private String transactionCode;

	@Column(name = "daily_amount", precision = 15, scale = 2)
	@Schema(description = "일 사용 금액")
	@Builder.Default
	private BigDecimal dailyAmount = BigDecimal.ZERO;
}
