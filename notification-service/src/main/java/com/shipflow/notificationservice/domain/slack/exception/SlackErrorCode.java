package com.shipflow.notificationservice.domain.slack.exception;

import org.springframework.http.HttpStatus;

import com.shipflow.common.exception.ErrorCode;

public enum SlackErrorCode implements ErrorCode {
	//슬랙 발송
	SLACK_MESSAGE_NOT_FOUND("SLACK_MESSAGE_NOT_FOUND", HttpStatus.NOT_FOUND, "슬랙 메시지를 찾을 수 없습니다."),
	SLACK_SEND_FAILED("SLACK_SEND_FAILED", HttpStatus.BAD_REQUEST, "슬랙 메시지 발송에 실패했습니다."),
	INVALID_SLACK_ID_FORMAT("INVALID_SLACK_ID_FORMAT", HttpStatus.BAD_REQUEST, "지원하지 않는 Slack ID 형식입니다."),
	RECEIVER_SLACK_ID_REQUIRED("RECEIVER_SLACK_ID_REQUIRED", HttpStatus.BAD_REQUEST, "receiverSlackId는 필수입니다."),
	//슬랙 수정
	SLACK_CHANNEL_ID_REQUIRED("SLACK_CHANNEL_ID_REQUIRED", HttpStatus.BAD_REQUEST, "slackChannelId는 필수입니다."),
	SLACK_TS_REQUIRED("SLACK_TS_REQUIRED", HttpStatus.BAD_REQUEST, "slackTs는 필수입니다."),
	SLACK_MESSAGE_REQUIRED("SLACK_MESSAGE_REQUIRED", HttpStatus.BAD_REQUEST, "message는 필수입니다."),
	SLACK_MESSAGE_UPDATE_FAILED("SLACK_MESSAGE_UPDATE_FAILED", HttpStatus.BAD_REQUEST, "슬랙 메시지 수정에 실패했습니다."),
	//슬랙 삭제
	SLACK_MESSAGE_DELETE_FAILED("SLACK_MESSAGE_DELETE_FAILED", HttpStatus.BAD_REQUEST, "슬랙 메시지 삭제에 실패했습니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	SlackErrorCode(String code, HttpStatus status, String message) {
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