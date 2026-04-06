package com.shipflow.shipmentservice.application.dto.command;

import java.math.BigDecimal;

import com.shipflow.shipmentservice.domain.ShipmentRouteStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentRouteUpdateCommand {

	private ShipmentRouteStatus status;
	private BigDecimal actualDistance;
}
