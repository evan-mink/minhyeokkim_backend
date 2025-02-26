package com.example.remittance.common.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 암/복호화 util class
 * @author evan.m.kim
 */
@Component
public class EncryptUtil {
	private static String SECRET_KEY;

	@Value("${remittance.secret-key}")
	public void setSecretKey(String secretKey) {
		SECRET_KEY = secretKey;
	}

	public static String encryptAES(String data) throws Exception {
		SecretKey sk = new SecretKeySpec(SECRET_KEY.substring(0, 16).getBytes(), "AES");
		IvParameterSpec iv = new IvParameterSpec(SECRET_KEY.substring(0, 16).getBytes());

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, sk, iv);
		byte[] encrypted = cipher.doFinal(data.trim().getBytes());

		return Base64.getEncoder().encodeToString(encrypted);
	}

	public static String decryptAES(String encData) throws Exception {
		SecretKey sk = new SecretKeySpec(SECRET_KEY.substring(0, 16).getBytes(), "AES");
		IvParameterSpec iv = new IvParameterSpec(SECRET_KEY.substring(0, 16).getBytes());

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, sk, iv);
		byte[] cipherEnc = Base64.getDecoder().decode(encData.getBytes());

		return new String(cipher.doFinal(cipherEnc));
	}
}
