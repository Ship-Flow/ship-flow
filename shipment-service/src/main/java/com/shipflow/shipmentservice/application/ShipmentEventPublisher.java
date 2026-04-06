package com.shipflow.shipmentservice.application;

import com.shipflow.shipmentservice.domain.event.ShipmentCreatedEvent;
import com.shipflow.shipmentservice.domain.event.ShipmentCreationFailedEvent;

public interface ShipmentEventPublisher {

	void publishCreated(ShipmentCreatedEvent event);

	void publishCreationFailed(ShipmentCreationFailedEvent event);
}