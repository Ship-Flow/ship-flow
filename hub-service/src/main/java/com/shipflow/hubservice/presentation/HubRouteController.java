package com.shipflow.hubservice.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.hubservice.application.HubRouteService;
import com.shipflow.hubservice.presentation.dto.HubRouteRequest;
import com.shipflow.hubservice.presentation.dto.HubRouteResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hub-routes")
@RequiredArgsConstructor
public class HubRouteController {

	private final HubRouteService hubRouteService;

	@PostMapping
	public ResponseEntity<ApiResponse<HubRouteResponse.Detail>> createRoute(
		@RequestBody @Valid HubRouteRequest.Create request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(hubRouteService.createRoute(request)));
	}

	@GetMapping("/{routeId}")
	public ResponseEntity<ApiResponse<HubRouteResponse.Detail>> getRoute(@PathVariable UUID routeId) {
		return ResponseEntity.ok(ApiResponse.ok(hubRouteService.getRoute(routeId)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<HubRouteResponse.Summary>>> getRoutes() {
		return ResponseEntity.ok(ApiResponse.ok(hubRouteService.getRoutes()));
	}

	@PatchMapping("/{routeId}")
	public ResponseEntity<ApiResponse<HubRouteResponse.Detail>> updateRoute(
		@PathVariable UUID routeId,
		@RequestBody @Valid HubRouteRequest.Update request) {
		return ResponseEntity.ok(ApiResponse.ok(hubRouteService.updateRoute(routeId, request)));
	}

	@DeleteMapping("/{routeId}")
	public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable UUID routeId) {
		hubRouteService.deleteRoute(routeId);
		return ResponseEntity.ok(ApiResponse.ok(null));
	}
}
