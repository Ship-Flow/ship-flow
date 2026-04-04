package com.shipflow.shipmentservice.application.dto;

import com.shipflow.shipmentservice.domain.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentUpdateCommand {

	private ShipmentStatus status;
}
