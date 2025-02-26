package com.example.remittance.integration.bank.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.remittance.bank.model.BankDto;
import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.common.model.entity.CodeBank;
import com.example.remittance.common.repository.CodeBankRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 은행 정보 rest controller API test
 * @author evan.m.kim
 */

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")    // IntelliJ version issue
@SpringBootTest(properties = "spring.profiles.active:local")
@AutoConfigureMockMvc
@Transactional
public class BankControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CodeBankRepository codeBankRepository;

	/***
	 * 은행 리스트 조회
	 * @throws Exception
	 */
	@Test
	public void selectBankList() throws Exception {
		// when
		ObjectMapper objectMapper = new ObjectMapper();

		MvcResult mvcResult = mockMvc.perform(get("/banks")
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		String responseBodyJson = mvcResult.getResponse().getContentAsString();

		BaseApiResponse<List<BankDto>> responseBody =
				objectMapper.readValue(responseBodyJson, new TypeReference<BaseApiResponse<List<BankDto>>>() {});

		List<BankDto> actualList = responseBody.getData();

		List<BankDto> expectedList = new ArrayList<>();

		for (CodeBank cb : codeBankRepository.findAll()) {
			expectedList.add(
					BankDto.builder()
							.bankCode(cb.getBankCode())
							.bankName(cb.getBankName())
							.build()
			);
		}

		// then
		assertNotNull(actualList);
		assertFalse(expectedList.isEmpty());
		assertEquals(expectedList.size(), actualList.size());

		for (int i = 0; i < expectedList.size(); i++) {
			BankDto expected = expectedList.get(i);
			BankDto actual = actualList.get(i);

			assertEquals(expected.getBankCode(), actual.getBankCode());
			assertEquals(expected.getBankName(), actual.getBankName());
		}
	}
}
