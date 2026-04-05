package com.shipflow.shipmentservice.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.shipmentservice.application.ShipmentManagerService;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerCreateResult;
import com.shipflow.shipmentservice.presentation.dto.request.PostShipmentManagerReqDto;
import com.shipflow.shipmentservice.presentation.dto.response.PostShipmentManagerResDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipment-managers")
public class ShipmentManagerController {

	private final ShipmentManagerService shipmentManagerService;

	@PostMapping
	public ApiResponse<PostShipmentManagerResDto> createShipmentManager(
		@Valid @RequestBody PostShipmentManagerReqDto request
	) {
		ShipmentManagerCreateResult result = shipmentManagerService.createShipmentManager(request.toCommand());
		return ApiResponse.ok(PostShipmentManagerResDto.fromResult(result));
	}
}
