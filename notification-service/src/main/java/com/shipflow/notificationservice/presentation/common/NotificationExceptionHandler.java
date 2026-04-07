package com.shipflow.notificationservice.presentation.common;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.common.exception.CommonErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@Order(1)
public class NotificationExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
		IllegalArgumentException e, HttpServletRequest request) {
		log.warn("IllegalArgumentException: {}", e.getMessage());
		return ResponseEntity
			.badRequest()
			.body(ApiResponse.fail(CommonErrorCode.VALIDATION_ERROR, e.getMessage(), request.getRequestURI()));
	}
}