package com.shipflow.common.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {

	VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
	INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "내부 오류가 발생했습니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	CommonErrorCode(String code, HttpStatus status, String message) {
		this.code = code;
		this.status = status;
		this.message = message;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public HttpStatus status() {
		return status;
	}

	@Override
	public String message() {
		return message;
	}
}