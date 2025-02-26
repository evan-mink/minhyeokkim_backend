package com.example.remittance.unit.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import com.example.remittance.common.util.DateUtil;
/**
 * 날짜 처리 관련 util class test
 * @author evan.m.kim
 */
class DateUtilTest {
	/***
	 * 현재 시간을 datetime 형식 format 문자열로 반환 test
	 */
	@Test
	public void now() {
		String now = DateUtil.now();
		String expectedFormat = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
		assertTrue(now.matches(expectedFormat));
	}

	/***
	 * 현재 시간에 pattern 적용하여 문자열로 반환 test
	 */
	@Test
	public void nowWithPattern() {
		String pattern = "yyyy-MM-dd";
		String expectedFormat = "\\d{4}-\\d{2}-\\d{2}";

		String actual = DateUtil.now(pattern);
		assertTrue(actual.matches(expectedFormat));

		String expected = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		assertEquals(actual, expected);
	}
}