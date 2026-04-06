package com.shipflow.shipmentservice.domain;

public enum ShipmentStatus {
	WAITING_AT_HUB,
	MOVING_TO_HUB,
	ARRIVED_AT_HUB,
	MOVING_TO_COMPANY,
	COMPLETED,
	CANCELLED
}
