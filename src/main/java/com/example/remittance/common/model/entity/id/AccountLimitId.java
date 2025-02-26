package com.example.remittance.common.model.entity.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 계좌-한도 entity key
 * @author evan.m.kim
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AccountLimitId implements Serializable {
	private Long accountId;
	private String transactionCode;
}
