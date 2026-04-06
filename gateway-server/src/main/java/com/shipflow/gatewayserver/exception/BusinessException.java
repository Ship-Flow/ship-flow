package com.shipflow.gatewayserver.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final GateErrorCode errorCode;

	public BusinessException(GateErrorCode errorCode) {
		super(errorCode.message());
		this.errorCode = errorCode;
	}

	public BusinessException(GateErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public BusinessException(GateErrorCode errorCode, Throwable cause) {
		super(errorCode.message(), cause);
		this.errorCode = errorCode;
	}

	public BusinessException(GateErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
}