package com.shipflow.shipmentservice.domain.exception;

import org.springframework.http.HttpStatus;

import com.shipflow.common.exception.ErrorCode;

public enum ShipmentErrorCode implements ErrorCode {

	// Shipment
	SHIPMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "배송을 찾을 수 없습니다."),
	INVALID_SHIPMENT_STATUS(HttpStatus.BAD_REQUEST, "잘못된 배송 상태입니다."),

	// ShipmentRoute
	SHIPMENT_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "배송 경로를 찾을 수 없습니다."),
	INVALID_SHIPMENT_ROUTE_STATUS(HttpStatus.BAD_REQUEST, "잘못된 배송 경로 상태입니다."),
	PREVIOUS_ROUTE_NOT_FOUND(HttpStatus.BAD_REQUEST, "이전 배송 경로가 존재하지 않아 도착 처리할 수 없습니다."),
	PREVIOUS_ROUTE_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "이전 배송 경로가 아직 도착 처리되지 않았습니다."),

	// ShipmentManager
	HUB_ID_REQUIRED_FOR_COMPANY_MANAGER(HttpStatus.BAD_REQUEST, "업체 배송 담당자는 hubId가 필수입니다."),

	// Validation
	INVALID_ACTUAL_DISTANCE(HttpStatus.BAD_REQUEST, "실제 이동 거리는 0보다 커야 합니다."),
	INVALID_PREVIOUS_ROUTE_TIME(HttpStatus.BAD_REQUEST, "이전 배송 경로의 시간 정보가 올바르지 않습니다."),
	INVALID_DURATION(HttpStatus.BAD_REQUEST, "계산된 소요 시간이 올바르지 않습니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	ShipmentErrorCode(HttpStatus status, String message) {
		this.code = this.name();
		this.status = status;
		this.message = message;
	}

	@Override
	public String code() {
		return this.code;
	}

	@Override
	public HttpStatus status() {
		return this.status;
	}

	@Override
	public String message() {
		return this.message;
	}
}
