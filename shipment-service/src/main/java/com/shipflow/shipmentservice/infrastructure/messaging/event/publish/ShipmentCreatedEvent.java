package com.shipflow.shipmentservice.infrastructure.messaging.event.publish;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;

@Getter
public class ShipmentCreatedEvent extends SagaEvent {

	private static final String EVENT_TYPE = "shipment.created";

	private UUID orderId;
	private UUID shipmentId;
	private UUID productId;
	private int quantity;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private LocalDateTime requestDeadline;
	private String requestNote;
	private List<Route> routes;

	public ShipmentCreatedEvent(UUID orderId, UUID shipmentId, UUID productId, int quantity,
		UUID departureHubId, UUID arrivalHubId, LocalDateTime requestDeadline, String requestNote,
		List<Route> routes) {
		super(EVENT_TYPE);
		this.orderId = orderId;
		this.shipmentId = shipmentId;
		this.productId = productId;
		this.quantity = quantity;
		this.departureHubId = departureHubId;
		this.arrivalHubId = arrivalHubId;
		this.requestDeadline = requestDeadline;
		this.requestNote = requestNote;
		this.routes = routes;
	}

	public record Route(int sequence, UUID departureHubId, UUID arrivalHubId) {
	}
}
