package com.shipflow.shipmentservice.application.dto.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.ShipmentRoute;
import com.shipflow.shipmentservice.domain.ShipmentRouteStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentRouteUpdateResult {

	private UUID shipmentRouteId;
	private UUID shipmentId;
	private ShipmentRouteStatus status;
	private BigDecimal actualDistance;
	private LocalDateTime updatedAt;

	public static ShipmentRouteUpdateResult fromEntity(ShipmentRoute route) {
		return ShipmentRouteUpdateResult.builder()
			.shipmentRouteId(route.getId())
			.shipmentId(route.getShipment().getId())
			.status(route.getStatus())
			.actualDistance(route.getActualDistance())
			.updatedAt(route.getUpdatedAt())
			.build();
	}
}
