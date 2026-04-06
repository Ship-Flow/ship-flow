package com.shipflow.shipmentservice.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentResult {
	private UUID shipmentId;
	private UUID orderId;
	private ShipmentStatus status;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private String shipmentAddress;
	private String recipientName;
	private String recipientSlackId;
	private ShipmentManagerResult deliveryManager;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Getter
	@Builder
	public static class ShipmentManagerResult {

		private UUID id;
		private String name;
		private String slackId;

		public static ShipmentManagerResult fromEntity(ShipmentManager manager) {
			return ShipmentManagerResult.builder()
				.id(manager.getId())
				.name(manager.getName())
				.slackId(manager.getSlackId())
				.build();
		}
	}

	public static ShipmentResult fromEntity(Shipment result) {
		return ShipmentResult.builder()
			.shipmentId(result.getId())
			.orderId(result.getOrderId())
			.status(result.getStatus())
			.departureHubId(result.getDepartureHubId())
			.arrivalHubId(result.getArrivalHubId())
			.shipmentAddress(result.getShipmentAddress())
			.recipientName(result.getRecipientName())
			.recipientSlackId(result.getRecipientSlackId())
			.deliveryManager(ShipmentManagerResult.fromEntity(result.getShipmentManager()))
			.createdAt(result.getCreatedAt())
			.updatedAt(result.getUpdatedAt())
			.build();
	}
}
