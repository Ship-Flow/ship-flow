package com.shipflow.shipmentservice.infrastructure.messaging.event.publish;

import java.util.UUID;

import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;

@Getter
public class ShipmentCreationFailedSagaEvent extends SagaEvent {

	private static final String EVENT_TYPE = "shipment.creation.failed";

	private final UUID orderId;

	public ShipmentCreationFailedSagaEvent(UUID orderId) {
		super(EVENT_TYPE);
		this.orderId = orderId;
	}
}