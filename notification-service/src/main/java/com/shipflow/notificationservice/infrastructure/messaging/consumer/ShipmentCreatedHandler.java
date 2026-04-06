package com.shipflow.notificationservice.infrastructure.messaging.consumer;

import org.springframework.stereotype.Component;

import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.notificationservice.application.NotificationOrchestratorService;
import com.shipflow.notificationservice.infrastructure.messaging.dto.ShipmentCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShipmentCreatedHandler extends AbstractSagaHandler<ShipmentCreatedEvent> {

	private final NotificationOrchestratorService notificationOrchestratorService;

	@Override
	protected void process(ShipmentCreatedEvent event) {
		notificationOrchestratorService.handleShipmentCreated(event);
	}
}