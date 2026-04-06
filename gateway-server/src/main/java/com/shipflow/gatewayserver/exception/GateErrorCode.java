package com.shipflow.gatewayserver.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GateErrorCode{

	ACCESS_DENIED(HttpStatus.FORBIDDEN, "G001", "해당 작업에 대한 권한이 없습니다."),
	MISSING_REALM_ACCESS(HttpStatus.UNAUTHORIZED, "G002", "realm_access claim이 없습니다."),
	MISSING_ROLES(HttpStatus.UNAUTHORIZED, "G003", "roles claim이 없거나 비어있습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	public String code() {
		return code;
	}
	
	public HttpStatus status() {
		return status;
	}

	public String message() {
		return message;
	}
}
