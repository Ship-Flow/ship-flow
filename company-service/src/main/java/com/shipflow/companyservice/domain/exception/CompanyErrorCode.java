package com.shipflow.companyservice.domain.exception;

import org.springframework.http.HttpStatus;

import com.shipflow.common.exception.ErrorCode;

public enum CompanyErrorCode implements ErrorCode {

	COMPANY_NOT_FOUND("COMPANY_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 업체를 찾을 수 없습니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	CompanyErrorCode(String code, HttpStatus status, String message) {
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
