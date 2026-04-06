package com.shipflow.shipmentservice.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentManagerSearchResult {

	private UUID shipmentManagerId;
	private UUID userId;
	private String name;
	private UUID hubId;
	private String slackId;
	private ShipmentManagerType type;
	private Integer shipmentSequence;
	private LocalDateTime createdAt;

	public static ShipmentManagerSearchResult fromEntity(ShipmentManager manager) {
		return ShipmentManagerSearchResult.builder()
			.shipmentManagerId(manager.getId())
			.userId(manager.getUserId())
			.name(manager.getName())
			.hubId(manager.getHubId())
			.slackId(manager.getSlackId())
			.type(manager.getType())
			.shipmentSequence(manager.getShipmentSequence())
			.createdAt(manager.getCreatedAt())
			.build();
	}
}
