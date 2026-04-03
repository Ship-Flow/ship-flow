package com.shipflow.orderservice.infrastructure.messaging.consume;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentCreatedEvent extends SagaEvent {

	private String orderId;
	private String shipmentId;
}
