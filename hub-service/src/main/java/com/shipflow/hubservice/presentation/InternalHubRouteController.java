package com.shipflow.hubservice.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.hubservice.application.HubRouteService;
import com.shipflow.hubservice.presentation.dto.HubRouteSegment;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/hub-routes")
@RequiredArgsConstructor
public class InternalHubRouteController {

	private final HubRouteService hubRouteService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<HubRouteSegment>>> getRoutePath(
		@RequestParam UUID departureHubId,
		@RequestParam UUID arrivalHubId) {
		List<HubRouteSegment> segments = hubRouteService.findPath(departureHubId, arrivalHubId);
		return ResponseEntity.ok(ApiResponse.ok(segments));
	}
}
