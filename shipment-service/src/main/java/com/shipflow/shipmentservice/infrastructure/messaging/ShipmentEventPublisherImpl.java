package com.shipflow.shipmentservice.infrastructure.messaging;

import org.springframework.stereotype.Component;

import com.shipflow.common.messaging.publisher.EventPublisher;
import com.shipflow.shipmentservice.application.ShipmentEventPublisher;
import com.shipflow.shipmentservice.domain.event.ShipmentCreatedEvent;
import com.shipflow.shipmentservice.domain.event.ShipmentCreationFailedEvent;
import com.shipflow.shipmentservice.infrastructure.messaging.event.publish.ShipmentCreatedSagaEvent;
import com.shipflow.shipmentservice.infrastructure.messaging.event.publish.ShipmentCreationFailedSagaEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShipmentEventPublisherImpl implements ShipmentEventPublisher {

	private final EventPublisher eventPublisher;

	@Override
	public void publishCreated(ShipmentCreatedEvent event) {
		eventPublisher.publish(new ShipmentCreatedSagaEvent(event));
	}

	@Override
	public void publishCreationFailed(ShipmentCreationFailedEvent event) {
		eventPublisher.publish(new ShipmentCreationFailedSagaEvent(event.orderId()));
	}
}