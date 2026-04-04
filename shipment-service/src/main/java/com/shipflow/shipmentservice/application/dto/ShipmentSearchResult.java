package com.shipflow.shipmentservice.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentSearchResult {

	private UUID shipmentId;
	private UUID orderId;
	private ShipmentStatus status;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private String recipientName;
	private UUID deliveryManagerId;
	private LocalDateTime createdAt;

	public static ShipmentSearchResult fromEntity(Shipment shipment) {
		return ShipmentSearchResult.builder()
			.shipmentId(shipment.getId())
			.orderId(shipment.getOrderId())
			.status(shipment.getStatus())
			.departureHubId(shipment.getDepartureHubId())
			.arrivalHubId(shipment.getArrivalHubId())
			.recipientName(shipment.getRecipientName())
			.deliveryManagerId(shipment.getShipmentManager().getId())
			.createdAt(shipment.getCreatedAt())
			.build();
	}
}
