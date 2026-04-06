package com.shipflow.shipmentservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.result.ShipmentSearchResult;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentSearchResDto {

	private UUID shipmentId;
	private UUID orderId;
	private ShipmentStatus status;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private String recipientName;
	private UUID deliveryManagerId;
	private LocalDateTime createdAt;

	public static ShipmentSearchResDto fromResult(ShipmentSearchResult result) {
		return ShipmentSearchResDto.builder()
			.shipmentId(result.getShipmentId())
			.orderId(result.getOrderId())
			.status(result.getStatus())
			.departureHubId(result.getDepartureHubId())
			.arrivalHubId(result.getArrivalHubId())
			.recipientName(result.getRecipientName())
			.deliveryManagerId(result.getDeliveryManagerId())
			.createdAt(result.getCreatedAt())
			.build();
	}
}
