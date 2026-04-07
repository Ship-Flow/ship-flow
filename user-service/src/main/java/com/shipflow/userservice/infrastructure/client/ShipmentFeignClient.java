package com.shipflow.userservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shipmentservice")
public interface ShipmentFeignClient {
	@PatchMapping("/internal/shipments/{userId}")
	ClientApiResponse<Void> patchManager(@PathVariable UUID userId);
}
