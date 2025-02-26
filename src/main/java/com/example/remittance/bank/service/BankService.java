package com.example.remittance.bank.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.example.remittance.bank.model.BankDto;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.common.service.CodeService;

/**
 * @author evan.m.kim
 */
@Service
@RequiredArgsConstructor
public class BankService {
	private final CodeService codeService;

	/***
	 * 은행 리스트 조회
	 * @return 은행 정보 DTO list
	 */
	public BaseApiResponse<List<BankDto>> selectBankList() {
		List<BankDto> bankList = new ArrayList<>();

		codeService.getCodeBankList()
				.forEach(src -> {
					bankList.add(
							BankDto.builder()
									.bankCode(src.getBankCode())
									.bankName(src.getBankName())
									.build()
					);
				});

		return BaseApiResponse.ok(bankList);
	}
}
