package com.shipflow.notificationservice.presentation.ai.external;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.notificationservice.application.ai.AiAppService;
import com.shipflow.notificationservice.application.ai.dto.result.AiLogResult;
import com.shipflow.notificationservice.presentation.ai.dto.request.GenerateDeadlineRequest;
import com.shipflow.notificationservice.presentation.ai.dto.response.AiLogResponse;
import com.shipflow.notificationservice.presentation.common.BasePageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiController {

	private final AiAppService aiAppService;

	public AiController(AiAppService aiAppService) {
		this.aiAppService = aiAppService;
	}

	// TODO: 이벤트 기반 전환 후 admin/debug 용으로 유지
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

	// TODO: 이벤트 기반 처리 완료 후 삭제 예정 (AI → Slack 테스트용)
	// 임시 컨트롤러
	@PostMapping("/test-slack")
	public ApiResponse<AiLogResponse> testSlack(
		@Valid @RequestBody GenerateDeadlineRequest request
	) {
		AiLogResult result = aiAppService.generateAndSendSlack(
			request.toCommand(),
			request.receiverSlackId()
		);

		return ApiResponse.ok(AiLogResponse.from(result));
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
	public ApiResponse<BasePageResponse<AiLogResponse>> getAiLogs(Pageable pageable) {
		return ApiResponse.ok(
			BasePageResponse.from(
				aiAppService.getAiLogs(pageable)
					.map(AiLogResponse::from)
			)
		);
	}
}