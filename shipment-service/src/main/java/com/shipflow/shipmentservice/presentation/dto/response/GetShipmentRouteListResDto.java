package com.shipflow.shipmentservice.presentation.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.result.ShipmentRouteResult;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetShipmentRouteListResDto {

	private UUID shipmentRouteId;
	private UUID shipmentId;
	private Integer sequence;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private BigDecimal estimatedDistance;
	private Integer estimatedDuration;
	private BigDecimal actualDistance;
	private Integer actualDuration;
	private String status;
	private ShipmentManagerDto shipmentManager;

	@Getter
	@Builder
	public static class ShipmentManagerDto {
		private UUID id;
		private String name;
		private String slackId;

		public static ShipmentManagerDto fromResult(
			ShipmentRouteResult.ShipmentManagerResult result
		) {
			if (result == null)
				return null;

			return ShipmentManagerDto.builder()
				.id(result.getId())
				.name(result.getName())
				.slackId(result.getSlackId())
				.build();
		}
	}

	public static GetShipmentRouteListResDto fromResult(ShipmentRouteResult result) {
		return GetShipmentRouteListResDto.builder()
			.shipmentRouteId(result.getShipmentRouteId())
			.shipmentId(result.getShipmentId())
			.sequence(result.getSequence())
			.departureHubId(result.getDepartureHubId())
			.arrivalHubId(result.getArrivalHubId())
			.estimatedDistance(result.getEstimatedDistance())
			.estimatedDuration(result.getEstimatedDuration())
			.actualDistance(result.getActualDistance())
			.actualDuration(result.getActualDuration())
			.status(result.getStatus().name())
			.shipmentManager(
				ShipmentManagerDto.fromResult(result.getShipmentManager())
			)
			.build();
	}
}
