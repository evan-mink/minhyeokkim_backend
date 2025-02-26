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
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * 거래-수수료 entity
 * @author evan.m.kim
 */

@Entity
@Table(name = "transaction_fee")
@Schema

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TransactionFee extends BaseEntity {
	@Id
	@Column(name = "transaction_id")
	@Schema(description = "거래 연번")
	private Long transactionId;

	@Column(name = "fee")
	@Schema(description = "수수료")
	private BigDecimal fee;
}
