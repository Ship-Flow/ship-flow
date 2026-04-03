package com.shipflow.shipmentservice.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.shipmentservice.application.ShipmentService;
import com.shipflow.shipmentservice.application.dto.ShipmentUpdateCommand;
import com.shipflow.shipmentservice.presentation.dto.GetShipmentResDto;
import com.shipflow.shipmentservice.presentation.dto.GetShipmentRouteListResDto;
import com.shipflow.shipmentservice.presentation.dto.PatchShipmentReqDto;
import com.shipflow.shipmentservice.presentation.dto.PatchShipmentResDto;
import com.shipflow.shipmentservice.presentation.dto.ShipmentSearchResDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ShipmentController {

	private final ShipmentService shipmentService;

	/**
	 * TODO: 권한에 따른 조회 조건 추가 필요
	 */
	@GetMapping("/api/shipments")
	public ApiResponse<List<ShipmentSearchResDto>> searchShipment(
		Pageable pageable
	) {
		List<ShipmentSearchResDto> shipmentList = shipmentService.searchShipment(pageable).stream()
			.map(ShipmentSearchResDto::fromResult)
			.toList();
		return ApiResponse.ok(shipmentList);
	}

	/**
	 * TODO: 권한에 따른 조회 조건 추가 필요
	 */
	@GetMapping("/api/shipments/{shipmentId}")
	public ApiResponse<GetShipmentResDto> getShipment(
		@PathVariable UUID shipmentId
	) {
		return ApiResponse.ok(GetShipmentResDto.fromResult(shipmentService.getShipment(shipmentId)));
	}

	@GetMapping("/api/shipments/{shipmentId}/routes")
	public ApiResponse<List<GetShipmentRouteListResDto>> getShipmentRoutes(
		@PathVariable UUID shipmentId
	) {
		List<GetShipmentRouteListResDto> routes = shipmentService.getShipmentRoutes(shipmentId).stream()
			.map(GetShipmentRouteListResDto::fromResult)
			.toList();
		return ApiResponse.ok(routes);
	}

	/**
	 * TODO: 권한에 따른 조건 추가 필요
	 */
	@PatchMapping("/api/shipments/{shipmentId}")
	public ApiResponse<PatchShipmentResDto> updateShipment(
		@PathVariable UUID shipmentId,
		@Valid @RequestBody PatchShipmentReqDto request
	) {
		ShipmentUpdateCommand command = PatchShipmentReqDto.toCommand(request);
		return ApiResponse.ok(PatchShipmentResDto.fromResult(shipmentService.updateShipment(shipmentId, command)));
	}

}
