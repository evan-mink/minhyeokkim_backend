package com.example.remittance.integration.common.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.remittance.common.repository.CodeBankRepository;
import com.example.remittance.common.repository.CodeTransactionRepository;
import com.example.remittance.common.service.CodeService;

/**
 * 공통 code service test
 * @author evan.m.kim
 */

@SpringBootTest(properties = "spring.profiles.active:local")
class CodeServiceTest {
	@Autowired
	private CodeService codeService;

	@Autowired
	private CodeBankRepository codeBankRepository;

	@Autowired
	private CodeTransactionRepository codeTransactionRepository;

	/***
	 * load test
	 */
	@Test
	public void loadCodeCaches() {
		codeService.loadCodeCaches();

		Integer codeBankSize = codeBankRepository.findAll().size();
		Integer codeTransactionSize = codeTransactionRepository.findAll().size();

		assertEquals(codeBankSize, codeService.getCodeBankList().size());
		assertEquals(codeBankSize, codeService.getCodeBankMap().size());
		assertEquals(codeTransactionSize, codeService.getCodeTransactionList().size());
		assertEquals(codeTransactionSize, codeService.getCodeTransactionMap().size());
	}

	/***
	 * refresh test
	 */
	@Test
	public void refreshAllCaches() {
		codeService.refreshAllCaches();

		Integer codeBankSize = codeBankRepository.findAll().size();
		Integer codeTransactionSize = codeTransactionRepository.findAll().size();

		assertEquals(codeBankSize, codeService.getCodeBankList().size());
		assertEquals(codeBankSize, codeService.getCodeBankMap().size());
		assertEquals(codeTransactionSize, codeService.getCodeTransactionList().size());
		assertEquals(codeTransactionSize, codeService.getCodeTransactionMap().size());
	}

}