package com.example.remittance.bank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.remittance.bank.model.BankDto;
import com.example.remittance.bank.service.BankService;
import com.example.remittance.common.model.base.BaseApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 은행 정보 rest controller
 * @author evan.m.kim
 */

@Tag(name = "#bank", description = "은행 정보")
@RequestMapping("/banks")
@RestController
@RequiredArgsConstructor
public class BankController {
	private final BankService bankService;

	@Tag(name = "#bank")
	@Operation(summary = "bank - select list")
	@GetMapping(value = "")
	public BaseApiResponse<List<BankDto>> selectBankList() {
		return bankService.selectBankList();
	}

}
