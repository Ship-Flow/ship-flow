package com.shipflow.notificationservice.infrastructure.messaging.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentCreatedEvent {
	private UUID orderId;
	private UUID ordererId;
	private UUID supplierCompanyId;
	private UUID receiverCompanyId;
	private String receiverSlackId;
	private UUID productId;
	private Integer quantity;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private LocalDateTime requestDeadline;
	private String requestNote;
	private LocalDateTime occurredAt;
}