package com.shipflow.shipmentservice.infrastructure.messaging.event.consume;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreatedEvent extends SagaEvent {

	private UUID orderId;
	private UUID ordererId;
	private UUID supplierCompanyId;
	private UUID receiverCompanyId;
	private UUID productId;
	private int quantity;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private LocalDateTime requestDeadline;
	private String requestNote;
	@JsonProperty("deliveryAddress")
	private String shipmentAddress;
}
