package com.shipflow.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleFlowShipException(
		BusinessException exception, HttpServletRequest request) {
		ErrorCode errorCode = exception.getErrorCode();
		log.warn("BusinessException: {} - {}", errorCode.code(), exception.getMessage());
		return ResponseEntity
			.status(errorCode.status())
			.body(ApiResponse.fail(errorCode, exception.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
		MethodArgumentNotValidException exception, HttpServletRequest request) {
		String detail = exception.getBindingResult().getFieldErrors().stream()
			.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
			.collect(Collectors.joining(", "));
		log.warn("Validation failed: {}", detail);
		return ResponseEntity
			.badRequest()
			.body(ApiResponse.fail(CommonErrorCode.VALIDATION_ERROR, detail, request.getRequestURI()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(
		Exception exception, HttpServletRequest request) {
		log.error("Unhandled Exception", exception);
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.fail(CommonErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI()));
	}
}