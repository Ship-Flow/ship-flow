package com.shipflow.notificationservice.presentation.ai.external;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.application.ai.AiAppService;
import com.shipflow.notificationservice.application.ai.dto.command.SearchAiLogCommand;
import com.shipflow.notificationservice.domain.ai.exception.AiErrorCode;
import com.shipflow.notificationservice.domain.ai.type.AiRequestStatus;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;
import com.shipflow.notificationservice.presentation.ai.dto.request.GenerateDeadlineRequest;
import com.shipflow.notificationservice.presentation.ai.dto.response.AiLogResponse;
import com.shipflow.notificationservice.presentation.common.BasePageRequest;
import com.shipflow.notificationservice.presentation.common.BasePageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiController {

	private final AiAppService aiAppService;

	public AiController(AiAppService aiAppService) {
		this.aiAppService = aiAppService;
	}

	// debug 용으로 유지
	@PostMapping("/debug")
	public ApiResponse<AiLogResponse> generateAiLog(
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole,
		@Valid @RequestBody GenerateDeadlineRequest request
	) {

		if (!"MASTER".equals(userRole)) {
			throw new BusinessException(AiErrorCode.FORBIDDEN_AI_ACCESS);
		}

		return ApiResponse.ok(
			AiLogResponse.from(
				aiAppService.generateAiLog(request.toCommand(UUID.fromString(userId)))
			)
		);
	}

	@GetMapping("/{aiId}")
	public ApiResponse<AiLogResponse> getAiLog(
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole,
		@PathVariable UUID aiId
	) {
		return ApiResponse.ok(
			AiLogResponse.from(
				aiAppService.getAiLog(
					UUID.fromString(userId),
					userRole,
					aiId
				)
			)
		);
	}

	@GetMapping
	public ApiResponse<BasePageResponse<AiLogResponse>> getAiLogs(
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole,
		@RequestParam(required = false) UUID shipmentManagerId,
		@RequestParam(required = false) AiRequestType requestType,
		@RequestParam(required = false) AiRequestStatus requestStatus,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtFrom,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtTo,
		BasePageRequest pageRequest
	) {
		Page<AiLogResponse> page = aiAppService.getAiLogs(
				new SearchAiLogCommand(
					UUID.fromString(userId),
					userRole,
					shipmentManagerId,
					requestType,
					requestStatus,
					workDate,
					createdAtFrom,
					createdAtTo
				),
				pageRequest.toPageable()
			)
			.map(AiLogResponse::from);

		return ApiResponse.ok(BasePageResponse.from(page));
	}
}