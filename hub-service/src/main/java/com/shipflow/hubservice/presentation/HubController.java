package com.shipflow.hubservice.presentation;

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
import com.shipflow.hubservice.application.HubService;
import com.shipflow.hubservice.presentation.dto.HubRequest;
import com.shipflow.hubservice.presentation.dto.HubResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {

	private final HubService hubService;

	@PostMapping
	public ResponseEntity<ApiResponse<HubResponse.Detail>> createHub(
		@RequestBody @Valid HubRequest.Create request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(hubService.createHub(request)));
	}

	@GetMapping("/{hubId}")
	public ResponseEntity<ApiResponse<HubResponse.Detail>> getHub(@PathVariable UUID hubId) {
		return ResponseEntity.ok(ApiResponse.ok(hubService.getHub(hubId)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<java.util.List<HubResponse.Summary>>> getHubs() {
		return ResponseEntity.ok(ApiResponse.ok(hubService.getHubs()));
	}

	@PatchMapping("/{hubId}")
	public ResponseEntity<ApiResponse<HubResponse.Detail>> updateHub(
		@PathVariable UUID hubId,
		@RequestBody @Valid HubRequest.Update request) {
		return ResponseEntity.ok(ApiResponse.ok(hubService.updateHub(hubId, request)));
	}

	@DeleteMapping("/{hubId}")
	public ResponseEntity<ApiResponse<Void>> deleteHub(@PathVariable UUID hubId) {
		hubService.deleteHub(hubId);
		return ResponseEntity.ok(ApiResponse.ok(null));
	}
}
