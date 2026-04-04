package com.shipflow.shipmentservice.presentation.dto;

import java.math.BigDecimal;

import com.shipflow.shipmentservice.application.dto.ShipmentRouteUpdateCommand;
import com.shipflow.shipmentservice.domain.ShipmentRouteStatus;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PatchShipmentRouteReqDto {

	@NotNull(message = "배송 상태는 필수입니다.")
	private ShipmentRouteStatus status;

	private BigDecimal actualDistance;

	@AssertTrue(message = "ARRIVED_AT_HUB 상태일 경우 actualDistance는 필수입니다.")
	public boolean isValidActualDistance() {
		if (status == ShipmentRouteStatus.ARRIVED_AT_HUB) {
			return actualDistance != null && actualDistance.signum() > 0;
		}
		return true;
	}

	public ShipmentRouteUpdateCommand toCommand() {
		return ShipmentRouteUpdateCommand.builder()
			.status(status)
			.actualDistance(actualDistance)
			.build();
	}
}
