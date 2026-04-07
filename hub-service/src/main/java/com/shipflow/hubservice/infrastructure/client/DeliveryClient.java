package com.shipflow.hubservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "deliveryservice")
public interface DeliveryClient {

	@DeleteMapping("/internal/delivery-managers/hubs/{hubId}")
	void deleteCompanyDeliveryManagers(
		@RequestHeader("X-Internal-Request") String internalRequest,
		@RequestHeader("X-User-Id") String requestUserId,
		@PathVariable("hubId") UUID hubId
	);
}
