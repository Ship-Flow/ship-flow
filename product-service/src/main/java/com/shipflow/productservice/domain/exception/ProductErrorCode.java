package com.shipflow.productservice.domain.exception;

import org.springframework.http.HttpStatus;

import com.shipflow.common.exception.ErrorCode;

public enum ProductErrorCode implements ErrorCode {

	PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
	INVALID_STOCK_VALUE("INVALID_STOCK_VALUE", HttpStatus.BAD_REQUEST, "잘못된 재고값입니다."),
	INVALID_ORDER_QUANTITY("INVALID_ORDER_QUANTITY", HttpStatus.BAD_REQUEST, "잘못된 수량입니다."),
	INACTIVE_PRODUCT("INACTIVE_PRODUCT", HttpStatus.BAD_REQUEST, "현재 비활성화된 상품입니다."),
	EXCEEDS_STOCK_LEVEL("EXCEEDS_STOCK_LEVEL", HttpStatus.BAD_REQUEST, "요청하신 주문량이 잔여 재고량보다 많습니다."),
	PRODUCT_NOT_OWNED_BY_COMPANY("PRODUCT_NOT_OWNED_BY_COMPANY", HttpStatus.BAD_REQUEST, "상품이 해당 회사에 소속되어 있지 않습니다."),
	COMPANY_ID_REQUIRED("COMPANY_ID_REQUIRED", HttpStatus.BAD_REQUEST, "회사 ID가 필요합니다."),
	UNAUTHORIZED("UNAUTHORIZED", HttpStatus.FORBIDDEN, "허가되지 않은 접근입니다.");

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
