package com.shipflow.shipmentservice.application.client;

import java.util.List;
import java.util.UUID;

import com.shipflow.shipmentservice.application.client.dto.HubRouteResult;

public interface HubClient {
	List<HubRouteResult> getHubRoutes(UUID departureHubId, UUID arrivalHubId);
}
