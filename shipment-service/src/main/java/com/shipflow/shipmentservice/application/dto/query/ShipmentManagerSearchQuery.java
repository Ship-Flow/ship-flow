package com.shipflow.shipmentservice.application.dto.query;

import java.util.UUID;

import com.shipflow.shipmentservice.domain.ShipmentManagerType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentManagerSearchQuery {

	private ShipmentManagerType type;
	private UUID hubId;
}
