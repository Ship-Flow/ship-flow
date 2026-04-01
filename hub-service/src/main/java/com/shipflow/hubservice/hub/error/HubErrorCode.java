package com.shipflow.hubservice.hub.error;

import com.shipflow.common.exception.ErrorCode;

import org.springframework.http.HttpStatus;

public enum HubErrorCode implements ErrorCode {

	// 조회
	HUB_NOT_FOUND("HUB_101", HttpStatus.NOT_FOUND, "허브를 찾을 수 없습니다."),
	HUB_ROUTE_NOT_FOUND("HUB_102", HttpStatus.NOT_FOUND, "허브 경로를 찾을 수 없습니다."),

	// 생성/수정 유효성
	DUPLICATE_HUB_NAME("HUB_201", HttpStatus.CONFLICT, "이미 존재하는 허브 이름입니다."),
	SAME_SOURCE_DESTINATION("HUB_202", HttpStatus.BAD_REQUEST, "출발 허브와 도착 허브가 동일합니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	HubErrorCode(String code, HttpStatus status, String message) {
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