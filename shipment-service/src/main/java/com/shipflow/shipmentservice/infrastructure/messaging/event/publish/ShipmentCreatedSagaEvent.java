package com.shipflow.shipmentservice.infrastructure.messaging.event.publish;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.shipflow.common.messaging.event.SagaEvent;
import com.shipflow.shipmentservice.domain.event.ShipmentCreatedEvent;

import lombok.Getter;

@Getter
public class ShipmentCreatedSagaEvent extends SagaEvent {

	private static final String EVENT_TYPE = "shipment.created";

	private final UUID orderId;
	private final UUID shipmentId;
	private final UUID productId;
	private final int quantity;
	private final UUID departureHubId;
	private final UUID arrivalHubId;
	private final LocalDateTime requestDeadline;
	private final String requestNote;
	private final String shipmentManagerSlackId;
	private final List<Route> routes;

	public ShipmentCreatedSagaEvent(ShipmentCreatedEvent event) {
		super(EVENT_TYPE);
		this.orderId = event.orderId();
		this.shipmentId = event.shipmentId();
		this.productId = event.productId();
		this.quantity = event.quantity();
		this.departureHubId = event.departureHubId();
		this.arrivalHubId = event.arrivalHubId();
		this.requestDeadline = event.requestDeadline();
		this.requestNote = event.requestNote();
		this.shipmentManagerSlackId = event.shipmentManagerSlackId();
		this.routes = event.routes().stream()
			.map(r -> new Route(r.sequence(), r.departureHubId(), r.arrivalHubId()))
			.toList();
	}

	public record Route(int sequence, UUID departureHubId, UUID arrivalHubId) {
	}
}
