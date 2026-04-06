package com.shipflow.shipmentservice.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.shipmentservice.application.ShipmentService;
import com.shipflow.shipmentservice.application.dto.result.ShipmentCompleteResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentRouteUpdateResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentUpdateResult;
import com.shipflow.shipmentservice.presentation.dto.response.GetShipmentResDto;
import com.shipflow.shipmentservice.presentation.dto.response.GetShipmentRouteListResDto;
import com.shipflow.shipmentservice.presentation.dto.request.PatchShipmentReqDto;
import com.shipflow.shipmentservice.presentation.dto.response.PatchShipmentResDto;
import com.shipflow.shipmentservice.presentation.dto.request.PatchShipmentRouteReqDto;
import com.shipflow.shipmentservice.presentation.dto.response.PatchShipmentRouteResDto;
import com.shipflow.shipmentservice.presentation.dto.response.ShipmentCompleteResDto;
import com.shipflow.shipmentservice.presentation.dto.response.ShipmentSearchResDto;

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

	/**
	 * TODO: 권한 처리 필요
	 */
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
	 * TODO: 권한 처리 필요
	 */
	@PatchMapping("/api/shipments/{shipmentId}")
	public ApiResponse<PatchShipmentResDto> updateShipment(
		@PathVariable UUID shipmentId,
		@Valid @RequestBody PatchShipmentReqDto request
	) {
		ShipmentUpdateResult result = shipmentService.updateShipment(shipmentId, request.toCommand());
		return ApiResponse.ok(PatchShipmentResDto.fromResult(result));
	}

	/**
	 * TODO: 권한 처리 필요
	 */
	@PatchMapping("/api/shipments/{shipmentId}/routes/{routeId}")
	public ApiResponse<PatchShipmentRouteResDto> updateShipmentRoute(
		@PathVariable UUID shipmentId,
		@PathVariable UUID routeId,
		@Valid @RequestBody PatchShipmentRouteReqDto request
	) {
		ShipmentRouteUpdateResult result = shipmentService.updateShipmentRoute(
			shipmentId,
			routeId,
			request.toCommand()
		);

		return ApiResponse.ok(PatchShipmentRouteResDto.fromResult(result));
	}

	@PostMapping("/api/shipments/{shipmentId}/complete")
	public ApiResponse<ShipmentCompleteResDto> completeShipment(
		@PathVariable UUID shipmentId
	) {
		ShipmentCompleteResult result = shipmentService.completeShipment(shipmentId);
		return ApiResponse.ok(ShipmentCompleteResDto.fromResult(result));
	}
}
