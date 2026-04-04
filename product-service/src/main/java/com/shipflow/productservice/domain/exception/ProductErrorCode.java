package com.shipflow.productservice.domain.exception;

import org.springframework.http.HttpStatus;

import com.shipflow.common.exception.ErrorCode;

public enum ProductErrorCode implements ErrorCode {

	PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
	INVALID_STOCK_VALUE("INVALID_STOCK_VALUE", HttpStatus.BAD_REQUEST, "잘못된 재고값입니다."),
	INVALID_ORDER_QUANTITY("INVALID_ORDER_QUANTITY", HttpStatus.BAD_REQUEST, "잘못된 주문량입니다."),
	OUT_OF_STOCK("OUT_OF_STOCK", HttpStatus.BAD_REQUEST, "요청하신 주문량이 잔여 재고량보다 많습니다.");

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
