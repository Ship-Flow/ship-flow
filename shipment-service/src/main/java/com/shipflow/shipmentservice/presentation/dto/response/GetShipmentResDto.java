package com.shipflow.shipmentservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.result.ShipmentResult;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetShipmentResDto {

	private UUID shipmentId;
	private UUID orderId;
	private ShipmentStatus status;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private String shipmentAddress;
	private String recipientName;
	private String recipientSlackId;
	private DeliveryManagerResDto deliveryManager;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Getter
	@Builder
	public static class DeliveryManagerResDto {

		private UUID id;
		private String name;
		private String slackId;

		public static DeliveryManagerResDto fromResult(ShipmentResult.ShipmentManagerResult manager) {
			return DeliveryManagerResDto.builder()
				.id(manager.getId())
				.name(manager.getName())
				.slackId(manager.getSlackId())
				.build();
		}
	}

	public static GetShipmentResDto fromResult(ShipmentResult result) {
		return GetShipmentResDto.builder()
			.shipmentId(result.getShipmentId())
			.orderId(result.getOrderId())
			.status(result.getStatus())
			.departureHubId(result.getDepartureHubId())
			.arrivalHubId(result.getArrivalHubId())
			.shipmentAddress(result.getShipmentAddress())
			.recipientName(result.getRecipientName())
			.recipientSlackId(result.getRecipientSlackId())
			.deliveryManager(DeliveryManagerResDto.fromResult(result.getDeliveryManager()))
			.createdAt(result.getCreatedAt())
			.updatedAt(result.getUpdatedAt())
			.build();
	}
}
