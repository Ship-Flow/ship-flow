package com.shipflow.notificationservice.domain.ai.exception;

import org.springframework.http.HttpStatus;

import com.shipflow.common.exception.ErrorCode;

public enum AiErrorCode implements ErrorCode {

	// 조회
	FORBIDDEN_AI_ACCESS("FORBIDDEN_AI_ACCESS", HttpStatus.FORBIDDEN, "AI 접근 권한이 없습니다."),
	AI_LOG_NOT_FOUND("AI_LOG_NOT_FOUND", HttpStatus.NOT_FOUND, "AI 로그를 찾을 수 없습니다."),

	// 이벤트
	AI_EVENT_NOT_FOUND("AI_EVENT_NOT_FOUND", HttpStatus.BAD_REQUEST, "AI 이벤트를 찾을 수 없습니다."),
	AI_EVENT_INVALID("AI_EVENT_INVALID", HttpStatus.BAD_REQUEST, "유효하지 않은 AI 이벤트입니다."),
	AI_REQUEST_TYPE_REQUIRED("AI_REQUEST_TYPE_REQUIRED", HttpStatus.BAD_REQUEST, "AI 요청 타입은 필수입니다."),

	// 필수 데이터
	AI_PROMPT_REQUIRED("AI_PROMPT_REQUIRED", HttpStatus.BAD_REQUEST, "AI 프롬프트는 필수입니다."),
	AI_FROM_HUB_REQUIRED("AI_FROM_HUB_REQUIRED", HttpStatus.BAD_REQUEST, "출발 허브 정보는 필수입니다."),
	AI_TO_HUB_REQUIRED("AI_TO_HUB_REQUIRED", HttpStatus.BAD_REQUEST, "도착 허브 정보는 필수입니다."),
	AI_PRODUCT_REQUIRED("AI_PRODUCT_REQUIRED", HttpStatus.BAD_REQUEST, "상품 정보는 필수입니다."),
	AI_DEADLINE_REQUIRED("AI_DEADLINE_REQUIRED", HttpStatus.BAD_REQUEST, "납기 정보는 필수입니다."),

	// 생성 (Gemini 호출)
	AI_GENERATE_FAILED("AI_GENERATE_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "AI 생성에 실패했습니다."),
	AI_RESPONSE_EMPTY("AI_RESPONSE_EMPTY", HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답이 비어있습니다."),
	AI_RESPONSE_PARSE_FAILED("AI_RESPONSE_PARSE_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답 파싱에 실패했습니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	AiErrorCode(String code, HttpStatus status, String message) {
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