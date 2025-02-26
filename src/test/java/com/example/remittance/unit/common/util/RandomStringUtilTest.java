package com.example.remittance.unit.common.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.remittance.common.util.RandomStringUtil;

/**
 * 랜덤 문자열 util class test
 * @author evan.m.kim
 */

class RandomStringUtilTest {
	/***
	 * format 내 '#' 문자를 랜덤 정수 문자로 변환 test
	 */
	@Test
	public void generateAccountNumber() {
		// check null
		assertNull(RandomStringUtil.generateAccountNumber(null));
		assertNull(RandomStringUtil.generateAccountNumber(null));

		// check valid
		String format = "##-####";
		String accountNumber = RandomStringUtil.generateAccountNumber(format);

		assertNotNull(accountNumber);
		assertEquals(accountNumber.length(), format.length());
		assertTrue(accountNumber.matches("\\d{2}-\\d{4}"));
	}
}