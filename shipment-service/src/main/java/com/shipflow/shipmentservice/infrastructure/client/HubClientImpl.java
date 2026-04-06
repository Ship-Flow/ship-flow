package com.shipflow.shipmentservice.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.shipflow.shipmentservice.application.client.HubClient;
import com.shipflow.shipmentservice.application.client.dto.HubRouteResult;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HubClientImpl implements HubClient {

	private final HubFeignClient hubFeignClient;

	@Override
	public List<HubRouteResult> getHubRoutes(UUID departureHubId, UUID arrivalHubId) {
		return hubFeignClient.getHubRoutes(departureHubId, arrivalHubId).getData();
	}
}
