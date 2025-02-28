package com.example.remittance.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.remittance.common.model.base.BaseApiResponse;

/**
 * @author evan.m.kim
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<BaseApiResponse<?>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException ex) {
		List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
		String errorMessage = fieldErrorList.stream()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.collect(Collectors.joining(", "));

		BaseApiResponse<?> response = BaseApiResponse.error(errorMessage);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}
