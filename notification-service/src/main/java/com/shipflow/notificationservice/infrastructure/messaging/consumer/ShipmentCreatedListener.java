package com.shipflow.notificationservice.infrastructure.messaging.consumer;

import org.springframework.stereotype.Component;

import com.shipflow.notificationservice.infrastructure.messaging.dto.ShipmentCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShipmentCreatedListener {

	private final ShipmentCreatedHandler shipmentCreatedHandler;

	public void onShipmentCreated(ShipmentCreatedEvent event) {
		shipmentCreatedHandler.handle(event);
	}

}