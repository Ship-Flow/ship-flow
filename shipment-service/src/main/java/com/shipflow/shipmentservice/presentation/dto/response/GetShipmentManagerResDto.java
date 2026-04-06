package com.shipflow.shipmentservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerResult;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetShipmentManagerResDto {

	private UUID shipmentManagerId;
	private UUID userId;
	private String name;
	private UUID hubId;
	private String slackId;
	private ShipmentManagerType type;
	private Integer shipmentSequence;
	private LocalDateTime createdAt;

	public static GetShipmentManagerResDto fromResult(ShipmentManagerResult result) {
		return GetShipmentManagerResDto.builder()
			.shipmentManagerId(result.getShipmentManagerId())
			.userId(result.getUserId())
			.name(result.getName())
			.hubId(result.getHubId())
			.slackId(result.getSlackId())
			.type(result.getType())
			.shipmentSequence(result.getShipmentSequence())
			.createdAt(result.getCreatedAt())
			.build();
	}
}
