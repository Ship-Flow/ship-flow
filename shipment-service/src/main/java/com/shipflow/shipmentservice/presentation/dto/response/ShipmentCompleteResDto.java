package com.shipflow.shipmentservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.result.ShipmentCompleteResult;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentCompleteResDto {

	private UUID shipmentId;
	private UUID orderId;
	private ShipmentStatus status;
	private LocalDateTime completedAt;

	public static ShipmentCompleteResDto fromResult(ShipmentCompleteResult result) {
		return ShipmentCompleteResDto.builder()
			.shipmentId(result.getShipmentId())
			.orderId(result.getOrderId())
			.status(result.getStatus())
			.completedAt(result.getCompletedAt())
			.build();
	}
}
