package com.shipflow.shipmentservice.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.shipmentservice.application.ShipmentManagerService;
import com.shipflow.shipmentservice.application.dto.query.ShipmentManagerSearchQuery;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerCreateResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerResult;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;
import com.shipflow.shipmentservice.presentation.dto.request.PostShipmentManagerReqDto;
import com.shipflow.shipmentservice.presentation.dto.response.GetShipmentManagerResDto;
import com.shipflow.shipmentservice.presentation.dto.response.GetShipmentManagerSearchResDto;
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

	@GetMapping("/{managerId}")
	public ApiResponse<GetShipmentManagerResDto> getShipmentManager(@PathVariable UUID managerId) {
		ShipmentManagerResult result = shipmentManagerService.getShipmentManager(managerId);
		return ApiResponse.ok(GetShipmentManagerResDto.fromResult(result));
	}

	@GetMapping
	public ApiResponse<List<GetShipmentManagerSearchResDto>> searchShipmentManager(
		@RequestParam(required = false) ShipmentManagerType type,
		@RequestParam(required = false) UUID hubId,
		Pageable pageable
	) {
		ShipmentManagerSearchQuery query = ShipmentManagerSearchQuery.builder()
			.type(type)
			.hubId(hubId)
			.build();
		List<GetShipmentManagerSearchResDto> result = shipmentManagerService
			.searchShipmentManager(query, pageable).stream()
			.map(GetShipmentManagerSearchResDto::fromResult)
			.toList();
		return ApiResponse.ok(result);
	}

	@DeleteMapping("/{managerId}")
	public ApiResponse<Void> deleteShipmentManager(
		@PathVariable UUID managerId,
		@RequestHeader("X-User-Id") UUID userId
	) {
		shipmentManagerService.deleteShipmentManager(managerId, userId);
		return ApiResponse.ok(null);
	}
}
