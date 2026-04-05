package com.shipflow.userservice.domain.error;

import org.springframework.http.HttpStatus;

import com.shipflow.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

	DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "U001", "이미 존재하는 username입니다."),
	DUPLICATE_SLACK_ID(HttpStatus.BAD_REQUEST, "U002", "이미 존재하는 슬랙 아이디입니다."),
	INVALID_USER_STATUS(HttpStatus.BAD_REQUEST, "U003", "유효하지 않은 사용자 상태입니다."),
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "U004", "유효하지 않은 요청입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U005", "사용자를 찾을 수 없습니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "U006", "해당 작업에 대한 권한이 없습니다."),
	HUB_MANAGER_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "U007", "해당 허브관리자를 삭제할 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

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