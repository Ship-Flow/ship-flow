package com.shipflow.notificationservice.infrastructure.messaging.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shipflow.common.messaging.event.SagaEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentCreatedEvent extends SagaEvent {

	private UUID orderId;
	private UUID shipmentId;

	private UUID productId;
	private Integer quantity;

	private UUID departureHubId;
	private UUID arrivalHubId;

	private LocalDateTime requestDeadline;
	private String requestNote;

	//Todo: 생성 확인
	private String shipmentManagerSlackId;

	private List<RouteInfo> routes;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RouteInfo {
		private Integer sequence;
		private UUID departureHubId;
		private UUID arrivalHubId;
	}
}