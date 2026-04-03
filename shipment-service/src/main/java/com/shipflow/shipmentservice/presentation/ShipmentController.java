package com.shipflow.shipmentservice.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.shipmentservice.application.ShipmentService;
import com.shipflow.shipmentservice.presentation.dto.GetShipmentResDto;
import com.shipflow.shipmentservice.presentation.dto.ShipmentSearchResDto;

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

}
