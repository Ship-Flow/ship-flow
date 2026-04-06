package com.shipflow.shipmentservice.application.dto.command;

import java.util.UUID;

import com.shipflow.shipmentservice.domain.ShipmentManagerType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentManagerCreateCommand {

	private UUID userId;
	private UUID hubId;
	private ShipmentManagerType type;
}
