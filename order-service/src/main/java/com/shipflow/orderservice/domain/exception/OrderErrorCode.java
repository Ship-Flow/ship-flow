package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum OrderErrorCode implements ErrorCode {

    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATE("INVALID_ORDER_STATE", HttpStatus.CONFLICT, "유효하지 않은 주문 상태입니다."),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_INSUFFICIENT_STOCK("PRODUCT_INSUFFICIENT_STOCK", HttpStatus.CONFLICT, "재고가 부족합니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    COMPANY_NOT_FOUND("COMPANY_NOT_FOUND", HttpStatus.NOT_FOUND, "업체를 찾을 수 없습니다."),
    EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", HttpStatus.SERVICE_UNAVAILABLE, "외부 서비스 오류가 발생했습니다.");

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
