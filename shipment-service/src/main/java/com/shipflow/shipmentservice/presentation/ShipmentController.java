package com.shipflow.shipmentservice.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.shipmentservice.application.ShipmentService;
import com.shipflow.shipmentservice.presentation.dto.GetShipmentResDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ShipmentController {

	private final ShipmentService shipmentService;

	@GetMapping("/api/shipments/{shipmentId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<GetShipmentResDto> getShipment(
		@PathVariable UUID shipmentId
	) {
		return ApiResponse.ok(GetShipmentResDto.fromResult(shipmentService.getShipment(shipmentId)));
	}
}
