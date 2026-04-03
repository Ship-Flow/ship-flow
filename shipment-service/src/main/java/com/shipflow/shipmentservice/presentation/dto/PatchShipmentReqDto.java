package com.shipflow.shipmentservice.presentation.dto;

import com.shipflow.shipmentservice.application.dto.ShipmentUpdateCommand;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PatchShipmentReqDto {

	@NotNull(message = "status는 필수입니다.")
	private ShipmentStatus status;

	public static ShipmentUpdateCommand toCommand(PatchShipmentReqDto dto) {
		return ShipmentUpdateCommand.builder()
			.status(dto.getStatus())
			.build();
	}
}
