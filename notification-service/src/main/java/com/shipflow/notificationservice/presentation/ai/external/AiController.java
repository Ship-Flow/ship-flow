package com.shipflow.notificationservice.presentation.ai.external;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.notificationservice.application.ai.AiAppService;
import com.shipflow.notificationservice.presentation.ai.dto.request.GenerateDeadlineRequest;
import com.shipflow.notificationservice.presentation.ai.dto.response.AiLogResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiController {

	private final AiAppService aiAppService;

	public AiController(AiAppService aiAppService) {
		this.aiAppService = aiAppService;
	}

	@PostMapping
	public ApiResponse<AiLogResponse> generateAiLog(
		@Valid @RequestBody GenerateDeadlineRequest request
	) {
		return ApiResponse.ok(
			AiLogResponse.from(
				aiAppService.generateAiLog(request.toCommand())
			)
		);
	}

	@GetMapping("/{aiId}")
	public ApiResponse<AiLogResponse> getAiLog(@PathVariable UUID aiId) {
		return ApiResponse.ok(
			AiLogResponse.from(
				aiAppService.getAiLog(aiId)
			)
		);
	}

	@GetMapping
	public ApiResponse<Page<AiLogResponse>> getAiLogs(Pageable pageable) {
		return ApiResponse.ok(
			aiAppService.getAiLogs(pageable)
				.map(AiLogResponse::from)
		);
	}
}