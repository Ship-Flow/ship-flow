package com.shipflow.shipmentservice.infrastructure.messaging.event.publish;

import java.util.UUID;

import com.shipflow.common.messaging.event.SagaEvent;
import com.shipflow.shipmentservice.domain.event.ShipmentCompletedEvent;

import lombok.Getter;

@Getter
public class ShipmentCompletedSagaEvent extends SagaEvent {

	private static final String EVENT_TYPE = "shipment.completed";

	private final UUID orderId;
	private final UUID shipmentId;

	public ShipmentCompletedSagaEvent(ShipmentCompletedEvent event) {
		super(EVENT_TYPE);
		this.orderId = event.orderId();
		this.shipmentId = event.shipmentId();
	}
}
