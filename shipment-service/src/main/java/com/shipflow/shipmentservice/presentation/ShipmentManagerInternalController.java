package com.shipflow.shipmentservice.presentation;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.shipmentservice.application.ShipmentManagerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/shipment-managers")
public class ShipmentManagerInternalController {

	private final ShipmentManagerService shipmentManagerService;

	@DeleteMapping("/hubs/{hubId}")
	public ApiResponse<Void> markPendingDeletionByHub(@PathVariable UUID hubId) {
		shipmentManagerService.markPendingDeletionByHubId(hubId);
		return ApiResponse.ok(null);
	}

	@DeleteMapping("/users/{userId}")
	public ApiResponse<Void> markPendingDeletionByUser(@PathVariable UUID userId) {
		shipmentManagerService.markPendingDeletionByUserId(userId);
		return ApiResponse.ok(null);
	}
}
