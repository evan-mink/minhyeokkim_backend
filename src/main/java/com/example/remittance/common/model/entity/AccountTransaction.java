package com.example.remittance.common.model.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.example.remittance.common.model.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 계좌-거래 entity
 * @author evan.m.kim
 */

@Entity
@Table(name = "account_transaction")
@Schema

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AccountTransaction extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	@Schema(description = "거래 연번")
	private Long transactionId;

	@Column(name = "amount")
	@Schema(description = "거래 금액")
	private BigDecimal amount;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_code")
	@JsonIgnore
	private CodeTransaction codeTransaction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", referencedColumnName = "account_id")
	@JsonIgnore
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_account_id", referencedColumnName = "account_id")
	@JsonIgnore
	private Account targetAccount;
}
