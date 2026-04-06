package com.shipflow.shipmentservice.presentation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.ShipmentRouteUpdateResult;
import com.shipflow.shipmentservice.domain.ShipmentRouteStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PatchShipmentRouteResDto {

	private UUID shipmentRouteId;
	private UUID shipmentId;
	private ShipmentRouteStatus status;
	private BigDecimal actualDistance;
	private LocalDateTime updatedAt;

	public static PatchShipmentRouteResDto fromResult(ShipmentRouteUpdateResult result) {
		return PatchShipmentRouteResDto.builder()
			.shipmentRouteId(result.getShipmentRouteId())
			.shipmentId(result.getShipmentId())
			.status(result.getStatus())
			.actualDistance(result.getActualDistance())
			.updatedAt(result.getUpdatedAt())
			.build();
	}
}
