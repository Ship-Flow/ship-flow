package com.shipflow.shipmentservice.application.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HubRouteResult {
	private Integer sequence;
	private UUID departureHubId;
	private UUID arrivalHubId;
	private BigDecimal estimatedDistance;
	private Integer estimatedDuration;
}
