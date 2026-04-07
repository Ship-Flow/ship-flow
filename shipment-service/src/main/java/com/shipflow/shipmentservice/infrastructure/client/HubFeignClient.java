package com.shipflow.shipmentservice.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.shipmentservice.application.client.dto.HubRouteResult;

@FeignClient(name = "hub-service", path = "/internal")
public interface HubFeignClient {
	@GetMapping("/hub-routes")
	ApiResponse<List<HubRouteResult>> getHubRoutes(
		@RequestParam UUID departureHubId,
		@RequestParam UUID arrivalHubId
	);
}
