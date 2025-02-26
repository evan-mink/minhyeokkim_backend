package com.example.remittance.integration.common.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.remittance.common.util.EncryptUtil;

/**
 * 암/복호화 util class test
 * @author evan.m.kim
 */

@SpringBootTest(properties = "spring.profiles.active:local")
class EncryptUtilTest {
	/***
	 * 암/복호화 test
	 * @throws Exception
	 */
	@Test
	public void testEncryptAndDecryptAES() throws Exception {
		String text = "test text";

		String encryptText = EncryptUtil.encryptAES(text);
		assertNotNull(encryptText);

		String decryptText = EncryptUtil.decryptAES(encryptText);
		assertNotNull(decryptText);

		assertEquals(text, decryptText);
	}
}