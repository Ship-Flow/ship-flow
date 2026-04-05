package com.shipflow.shipmentservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.result.ShipmentUpdateResult;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PatchShipmentResDto {

	private UUID shipmentId;
	private UUID orderId;
	private ShipmentStatus status;
	private LocalDateTime updatedAt;

	public static PatchShipmentResDto fromResult(ShipmentUpdateResult result) {
		return PatchShipmentResDto.builder()
			.shipmentId(result.getShipmentId())
			.orderId(result.getOrderId())
			.status(result.getStatus())
			.updatedAt(result.getUpdatedAt())
			.build();
	}
}
