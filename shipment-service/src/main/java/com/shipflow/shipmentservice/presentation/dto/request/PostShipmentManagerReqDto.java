package com.shipflow.shipmentservice.presentation.dto.request;

import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.command.ShipmentManagerCreateCommand;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostShipmentManagerReqDto {

	@NotNull(message = "userId는 필수입니다.")
	private UUID userId;

	private UUID hubId;

	@NotNull(message = "type은 필수입니다.")
	private ShipmentManagerType type;

	public ShipmentManagerCreateCommand toCommand() {
		return ShipmentManagerCreateCommand.builder()
			.userId(userId)
			.hubId(hubId)
			.type(type)
			.build();
	}
}
