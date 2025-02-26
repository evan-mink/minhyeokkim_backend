package com.example.remittance.common.util;

import java.util.Random;

/**
 * 랜덤 문자열 util class
 * @author evan.m.kim
 */
public class RandomStringUtil {
	/***
	 * format 내 '#' 문자를 랜덤 정수 문자로 변환
	 *
	 * @param format 변환할 문자열 (ex: "###-##-#####")
	 * @return 변환된 문자열 (ex: "123-45-67890")
	 */
	public static String generateAccountNumber(String format) {
		if(format == null || format.isEmpty()) {
			return null;
		}

		Random random = new Random();
		StringBuilder sb = new StringBuilder(format.length());

		for (int i = 0; i < format.length(); i++) {
			char c = format.charAt(i);

			if (c == '#') {
				sb.append(random.nextInt(10));
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}
}
