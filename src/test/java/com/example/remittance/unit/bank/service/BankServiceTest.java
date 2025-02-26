package com.example.remittance.unit.bank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.remittance.bank.model.BankDto;
import com.example.remittance.bank.service.BankService;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.common.model.entity.CodeBank;
import com.example.remittance.common.service.CodeService;

/**
 * 은행 service test
 * @author evan.m.kim
 */

@ExtendWith(MockitoExtension.class)
class BankServiceTest {
	@InjectMocks
	private BankService bankService;

	@Mock
	private CodeService codeService;

	/***
	 * 은행 리스트 조회 test
	 */
	@Test
	public void selectBankList() {
		// given
		List<CodeBank> codeBankList = List.of(
				CodeBank.builder()
						.bankCode("001")
						.bankName("한국은행")
						.accountFormat("###############")
						.build(),
				CodeBank.builder()
						.bankCode("002")
						.bankName("산업은행")
						.accountFormat("###############")
						.build(),
				CodeBank.builder()
						.bankCode("003")
						.bankName("기업은행")
						.accountFormat("###-##-######")
						.build()
		);

		when(codeService.getCodeBankList()).thenReturn(codeBankList);

		// when
		BaseApiResponse<List<BankDto>> response = bankService.selectBankList();
		List<BankDto> actualList = response.getData();

		// then
		List<BankDto> expectedList = codeBankList
				.stream()
				.map(cb -> BankDto.builder()
							.bankCode(cb.getBankCode())
							.bankName(cb.getBankName())
							.build()
				).collect(Collectors.toList());

		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEquals(BaseApiResponse.ok().getMessage(), response.getMessage());

		assertEquals(actualList.size(), expectedList.size());
		assertEquals(actualList.get(0).getBankCode(), expectedList.get(0).getBankCode());
		assertEquals(actualList.get(0).getBankName(), expectedList.get(0).getBankName());
	}
}