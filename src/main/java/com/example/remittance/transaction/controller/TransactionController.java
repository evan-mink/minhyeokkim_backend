package com.example.remittance.transaction.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.example.remittance.common.model.base.BaseApiRequest;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.transaction.model.TransactionDto;
import com.example.remittance.transaction.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 거래 정보 rest controller
 * @author evan.m.kim
 */
@Tag(name = "#transaction", description = "거래 정보")
@RequestMapping("/transactions")
@RestController
@RequiredArgsConstructor
public class TransactionController {
	private final TransactionService transactionService;

	@Tag(name = "#transaction")
	@Operation(summary = "transaction - select list")
	@GetMapping(value = "")
	public BaseApiResponse<List<TransactionDto.Response>> selectTransactionList() {
		return transactionService.selectTransactionList();
	}

	@Tag(name = "#transaction")
	@Operation(summary = "transaction - select by id")
	@GetMapping(value = "{transactionId}")
	public BaseApiResponse<TransactionDto.Response> selectTransaction(@PathVariable Long transactionId) {
		return transactionService.selectTransaction(transactionId);
	}

	@Tag(name = "#transaction")
	@Operation(summary = "transaction - deposit")
	@PostMapping(value = "deposit")
	public BaseApiResponse<TransactionDto.Response> deposit(
			@RequestBody BaseApiRequest<TransactionDto.TransactionRequest> requestBody) {
		return transactionService.deposit(requestBody);
	}

	@Tag(name = "#transaction")
	@Operation(summary = "transaction - withdraw")
	@PostMapping(value = "withdraw")
	public BaseApiResponse<TransactionDto.Response> withdraw(
			@RequestBody BaseApiRequest<TransactionDto.TransactionRequest> requestBody) {
		return transactionService.withdraw(requestBody);
	}

	@Tag(name = "#transaction")
	@Operation(summary = "transaction - transfer")
	@PostMapping(value = "transfer")
	public BaseApiResponse<TransactionDto.Response> transfer(
			@RequestBody BaseApiRequest<TransactionDto.TransferRequest> requestBody) {
		return transactionService.transfer(requestBody);
	}
}
