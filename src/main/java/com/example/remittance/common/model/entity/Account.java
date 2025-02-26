package com.example.remittance.common.model.entity;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.example.remittance.common.converter.AESConverter;
import com.example.remittance.common.model.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 계좌 entity
 * @author evan.m.kim
 */

@Entity
@Table(name = "account")
@Schema

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Account extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	@Schema(description = "계좌 연번")
	private Long accountId;

	@Column(name = "account_number")
	@Schema(description = "계좌 번호")
	@Convert(converter = AESConverter.class)
	private String accountNumber;

	@Column(name = "balance", precision = 15, scale = 2)
	@Schema(description = "잔액")
	@Builder.Default
	private BigDecimal balance = BigDecimal.ZERO;

	@Column(name = "del_yn")
	@Schema(description = "삭제 여부")
	@Builder.Default
	private Integer delYn = 0;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_code")
	@JsonIgnore
	private CodeBank codeBank;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	@JsonIgnore
	private List<AccountLimit> accountLimitList;
}
