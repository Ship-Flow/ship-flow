package com.shipflow.shipmentservice.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentCanceledResult {

	private UUID shipmentId;
	private UUID orderId;
	private ShipmentStatus status;
	private LocalDateTime canceledAt;

	public static ShipmentCanceledResult fromEntity(Shipment shipment) {
		return ShipmentCanceledResult.builder()
			.shipmentId(shipment.getId())
			.orderId(shipment.getOrderId())
			.status(shipment.getStatus())
			.canceledAt(shipment.getUpdatedAt())
			.build();
	}
}
