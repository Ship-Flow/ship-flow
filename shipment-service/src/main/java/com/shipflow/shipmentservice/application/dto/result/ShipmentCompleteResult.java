package com.shipflow.shipmentservice.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentCompleteResult {

	private UUID shipmentId;
	private UUID orderId;
	private ShipmentStatus status;
	private LocalDateTime completedAt;

	public static ShipmentCompleteResult fromEntity(Shipment shipment) {
		return ShipmentCompleteResult.builder()
			.shipmentId(shipment.getId())
			.orderId(shipment.getOrderId())
			.status(shipment.getStatus())
			.completedAt(shipment.getUpdatedAt())
			.build();
	}
}
