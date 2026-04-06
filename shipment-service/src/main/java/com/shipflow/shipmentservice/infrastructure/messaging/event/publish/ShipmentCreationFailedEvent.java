package com.shipflow.shipmentservice.infrastructure.messaging.event.publish;

import java.util.UUID;

import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;

@Getter
public class ShipmentCreationFailedEvent extends SagaEvent {

	private static final String EVENT_TYPE = "shipment.creation.failed";

	private UUID orderId;

	public ShipmentCreationFailedEvent(UUID orderId) {
		super(EVENT_TYPE);
		this.orderId = orderId;
	}
}
