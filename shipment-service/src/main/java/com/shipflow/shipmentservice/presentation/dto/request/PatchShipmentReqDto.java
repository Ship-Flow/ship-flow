package com.shipflow.shipmentservice.presentation.dto.request;

import com.shipflow.shipmentservice.application.dto.command.ShipmentUpdateCommand;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PatchShipmentReqDto {

	@NotNull(message = "status는 필수입니다.")
	private ShipmentStatus status;

	public ShipmentUpdateCommand toCommand() {
		return ShipmentUpdateCommand.builder()
			.status(status)
			.build();
	}
}
