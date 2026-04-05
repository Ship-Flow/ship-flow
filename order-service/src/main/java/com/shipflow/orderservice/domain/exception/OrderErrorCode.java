package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATE("INVALID_ORDER_STATE", HttpStatus.CONFLICT, "유효하지 않은 주문 상태입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    OrderErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override public String code() { return code; }
    @Override public HttpStatus status() { return status; }
    @Override public String message() { return message; }
}
