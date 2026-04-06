package com.shipflow.hubservice.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HubRouteSegment(
	int sequence,
	UUID departureHubId,
	UUID arrivalHubId,
	BigDecimal estimatedDistance,
	int estimatedDuration
) {
}
