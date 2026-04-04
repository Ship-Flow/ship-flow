package com.shipflow.shipmentservice.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentRoute;
import com.shipflow.shipmentservice.domain.ShipmentRouteStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentRouteResult {

	private UUID shipmentRouteId;
	private UUID shipmentId;
	private Integer sequence;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private BigDecimal estimatedDistance;
	private Integer estimatedDuration;
	private BigDecimal actualDistance;
	private Integer actualDuration;
	private ShipmentRouteStatus status;
	private ShipmentManagerResult shipmentManager;

	@Getter
	@Builder
	public static class ShipmentManagerResult {
		private UUID id;
		private String name;
		private String slackId;

		public static ShipmentManagerResult fromEntity(ShipmentManager manager) {
			if (manager == null)
				return null;

			return ShipmentManagerResult.builder()
				.id(manager.getId())
				.name(manager.getName())
				.slackId(manager.getSlackId())
				.build();
		}
	}

	public static ShipmentRouteResult fromEntity(ShipmentRoute shipmentRoute) {
		return ShipmentRouteResult.builder()
			.shipmentRouteId(shipmentRoute.getId())
			.shipmentId(shipmentRoute.getShipment().getId())
			.sequence(shipmentRoute.getSequence())
			.departureHubId(shipmentRoute.getDepartureHubId())
			.arrivalHubId(shipmentRoute.getArrivalHubId())
			.estimatedDistance(shipmentRoute.getEstimatedDistance())
			.estimatedDuration(shipmentRoute.getEstimatedDuration())
			.actualDistance(shipmentRoute.getActualDistance())
			.actualDuration(shipmentRoute.getActualDuration())
			.status(shipmentRoute.getStatus())
			.shipmentManager(
				ShipmentManagerResult.fromEntity(shipmentRoute.getShipmentManager())
			)
			.build();
	}
}
