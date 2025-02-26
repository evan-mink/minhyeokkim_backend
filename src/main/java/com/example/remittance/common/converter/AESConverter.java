package com.example.remittance.common.converter;

import lombok.extern.slf4j.Slf4j;

import com.example.remittance.common.util.EncryptUtil;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Entity 문자열 field 암/복호화 converter
 * @author evan.m.kim
 */
@Converter
@Slf4j
public class AESConverter implements AttributeConverter<String, String> {
	@Override
	public String convertToDatabaseColumn(String data) {
		if (data == null) {
			return null;
		}

		try {
			return EncryptUtil.encryptAES(data);
		} catch (Exception e) {
			log.error("Encryption error for data: {}", data, e);
			throw new RuntimeException("Error encrypting data", e);
		}
	}

	@Override
	public String convertToEntityAttribute(String encData) {
		if (encData == null) {
			return null;
		}

		try {
			return EncryptUtil.decryptAES(encData);
		} catch (Exception e) {
			log.error("Decryption error for data: {}", encData, e);
			throw new RuntimeException("Error decrypting data", e);
		}
	}
}
