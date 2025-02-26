package com.example.remittance.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 날짜 처리 관련 util class
 *
 * @author evan.m.kim
 */
public class DateUtil {
	/**
	 * 현재 시간을 datetime 형식 format 문자열로 반환
	 *
	 * @return datetime 형식 문자열 (yyyy-MM-dd HH:mm:ss)
	 */
	public static String now() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 현재 시간에 pattern 적용하여 문자열로 반환
	 *
	 * @param pattern 적용하고자 하는 pattern 문자열 (ex: "yyyyMMdd")
	 * @return pattern 적용 문자열 (ex: "20210528")
	 */
	public static String now(String pattern) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
	}
}
