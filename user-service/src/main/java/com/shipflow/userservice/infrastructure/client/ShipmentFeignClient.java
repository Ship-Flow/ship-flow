package com.shipflow.userservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shipment-service")
public interface ShipmentFeignClient {
	@PatchMapping("/internal/shipments/{userId}")
	void patchManager(@PathVariable UUID userId);
}
