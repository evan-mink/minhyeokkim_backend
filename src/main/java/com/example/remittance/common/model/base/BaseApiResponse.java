package com.example.remittance.common.model.base;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.example.remittance.common.util.DateUtil;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

/**
 * API response wrapper class
 *
 * @author evan.m.kim
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseApiResponse<T> {
	@Schema(description = "transaction time")
	private String transactionTime;

	@Schema(description = "if successful, true")
	private boolean success;

	@Schema(description = "result http-status code")
	private Integer code;

	@Schema(description = "result message")
	private String message;

	@Schema(description = "json stringify string")
	@Valid
	private T data;

	private static final String DEFAULT_OK_MESSAGE = "OK";
	private static final String DEFAULT_ERROR_MESSAGE = "Bad Request";

	// OK
	public static <T> BaseApiResponse<T> ok() {
		return ok(HttpStatus.OK, DEFAULT_OK_MESSAGE);
	}

	public static <T> BaseApiResponse<T> ok(HttpStatus httpStatus, String message) {
		return ok(httpStatus, message, null);
	}

	public static <T> BaseApiResponse<T> ok(T data) {
		return ok(HttpStatus.OK, DEFAULT_OK_MESSAGE, data);
	}

	public static <T> BaseApiResponse<T> ok(HttpStatus httpStatus, String message, T data) {
		return (BaseApiResponse<T>)BaseApiResponse.builder()
				.transactionTime(DateUtil.now())
				.success(true)
				.code(httpStatus.value())
				.message(message)
				.data(data)
				.build();
	}

	// Error
	public static <T> BaseApiResponse<T> error() {
		return error(HttpStatus.BAD_REQUEST, DEFAULT_ERROR_MESSAGE);
	}

	public static <T> BaseApiResponse<T> error(String message) {
		return error(HttpStatus.BAD_REQUEST, message);
	}

	public static <T> BaseApiResponse<T> error(HttpStatus httpStatus, String message) {
		return (BaseApiResponse<T>)BaseApiResponse.builder()
			.transactionTime(DateUtil.now())
			.success(false)
			.code(httpStatus.value())
			.message(message)
			.build();
	}
}
