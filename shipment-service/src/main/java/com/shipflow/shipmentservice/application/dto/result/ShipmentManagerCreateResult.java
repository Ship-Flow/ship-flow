package com.shipflow.shipmentservice.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentManagerCreateResult {

	private UUID shipmentManagerId;
	private UUID userId;
	private UUID hubId;
	private String name;
	private String slackId;
	private ShipmentManagerType type;
	private Integer shipmentSequence;
	private LocalDateTime createdAt;

	public static ShipmentManagerCreateResult fromEntity(ShipmentManager shipmentManager) {
		return ShipmentManagerCreateResult.builder()
			.shipmentManagerId(shipmentManager.getId())
			.userId(shipmentManager.getUserId())
			.hubId(shipmentManager.getHubId())
			.name(shipmentManager.getName())
			.slackId(shipmentManager.getSlackId())
			.type(shipmentManager.getType())
			.shipmentSequence(shipmentManager.getShipmentSequence())
			.createdAt(shipmentManager.getCreatedAt())
			.build();
	}
}
