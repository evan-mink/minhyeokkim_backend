package com.example.remittance.account.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.example.remittance.account.model.AccountDto;
import com.example.remittance.account.service.AccountService;
import com.example.remittance.common.model.base.BaseApiRequest;
import com.example.remittance.common.model.base.BaseApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 계좌 정보 rest controller
 * @author evan.m.kim
 */

@Tag(name = "#account", description = "계좌 정보")
@RequestMapping("/accounts")
@RestController
@RequiredArgsConstructor
public class AccountController {
	private final AccountService accountService;

	@Tag(name = "#account")
	@Operation(summary = "account - select list")
	@GetMapping(value = "")
	public BaseApiResponse<List<AccountDto>> selectAccountList() {
		return accountService.selectAccountList();
	}

	@Tag(name = "#account")
	@Operation(summary = "account - select by id")
	@GetMapping(value = "{accountId}")
	public BaseApiResponse<AccountDto> selectAccount(@PathVariable Long accountId) {
		return accountService.selectAccount(accountId);
	}

	@Tag(name = "#account")
	@Operation(summary = "account - insert account")
	@PostMapping()
	public BaseApiResponse<AccountDto> insertAccount(
			@RequestBody BaseApiRequest<AccountDto> requestBody) {
		return accountService.insertAccount(requestBody);
	}

	@Tag(name = "#account")
	@Operation(summary = "account - delete by id")
	@DeleteMapping(value = "{accountId}")
	public BaseApiResponse<Void> deleteAccount(@PathVariable Long accountId) {
		return accountService.deleteAccount(accountId);
	}
}
