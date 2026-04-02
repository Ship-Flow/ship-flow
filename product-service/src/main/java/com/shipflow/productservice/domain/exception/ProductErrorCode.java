package com.shipflow.productservice.domain.exception;

import org.springframework.http.HttpStatus;

import com.shipflow.common.exception.ErrorCode;

public enum ProductErrorCode implements ErrorCode {

	PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND, "Product not found");

	private final String code;
	private final HttpStatus status;
	private final String message;

	ProductErrorCode(String code, HttpStatus status, String message) {
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
