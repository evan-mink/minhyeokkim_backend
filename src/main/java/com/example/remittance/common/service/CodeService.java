package com.example.remittance.common.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.example.remittance.common.model.entity.CodeBank;
import com.example.remittance.common.model.entity.CodeTransaction;
import com.example.remittance.common.repository.CodeBankRepository;
import com.example.remittance.common.repository.CodeTransactionRepository;

import jakarta.annotation.PostConstruct;

/**
 * 공통 코드 조회 service
 * - application load 시점에 조회하여 재사용합니다.
 *
 * @author evan.m.kim
 */
@Service
@RequiredArgsConstructor
public class CodeService {
	private final CodeBankRepository codeBankRepository;
	private final CodeTransactionRepository codeTransactionRepository;

	private List<CodeBank> codeBankList;
	private Map<String, CodeBank> codeBankMap;
	private List<CodeTransaction> codeTransactionList;
	private Map<String, CodeTransaction> codeTransactionMap;

	public List<CodeBank> getCodeBankList() {
		return this.codeBankList;
	}

	public Map<String, CodeBank> getCodeBankMap() {
		return this.codeBankMap;
	}

	public List<CodeTransaction> getCodeTransactionList() {
		return this.codeTransactionList;
	}

	public Map<String, CodeTransaction> getCodeTransactionMap() {
		return this.codeTransactionMap;
	}

	@PostConstruct
	public void loadCodeCaches() {
		this.loadCodeBankCache();
		this.loadCodeTransactionCache();
	}

	private void loadCodeBankCache() {
		this.codeBankList = codeBankRepository.findAll();

		this.codeBankMap = this.codeBankList.stream()
				.collect(Collectors.toMap(cb -> cb.getBankCode(), cb -> cb));
	}

	private void loadCodeTransactionCache() {
		this.codeTransactionList = codeTransactionRepository.findAll();

		this.codeTransactionMap = this.codeTransactionList.stream()
				.collect(Collectors.toMap(ct -> ct.getTransactionCode(), ct -> ct));
	}

	/***
	 * 캐시 재조회
	 */
	public void refreshAllCaches() {
		this.loadCodeCaches();
	}
}
