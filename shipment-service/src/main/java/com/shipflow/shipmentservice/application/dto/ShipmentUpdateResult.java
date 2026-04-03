package com.shipflow.shipmentservice.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentUpdateResult {

	private UUID shipmentId;
	private UUID orderId;
	private ShipmentStatus status;
	private LocalDateTime updatedAt;

	public static ShipmentUpdateResult fromEntity(Shipment shipment) {
		return ShipmentUpdateResult.builder()
			.shipmentId(shipment.getId())
			.orderId(shipment.getOrderId())
			.status(shipment.getStatus())
			.updatedAt(shipment.getUpdatedAt())
			.build();
	}
}
