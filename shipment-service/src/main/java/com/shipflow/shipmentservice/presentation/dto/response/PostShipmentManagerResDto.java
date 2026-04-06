package com.shipflow.shipmentservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerCreateResult;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostShipmentManagerResDto {

	private UUID shipmentManagerId;
	private UUID userId;
	private UUID hubId;
	private String name;
	private String slackId;
	private ShipmentManagerType type;
	private Integer shipmentSequence;
	private LocalDateTime createdAt;

	public static PostShipmentManagerResDto fromResult(ShipmentManagerCreateResult result) {
		return PostShipmentManagerResDto.builder()
			.shipmentManagerId(result.getShipmentManagerId())
			.userId(result.getUserId())
			.hubId(result.getHubId())
			.name(result.getName())
			.slackId(result.getSlackId())
			.type(result.getType())
			.shipmentSequence(result.getShipmentSequence())
			.createdAt(result.getCreatedAt())
			.build();
	}
}
