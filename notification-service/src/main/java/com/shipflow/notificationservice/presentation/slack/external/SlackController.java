package com.shipflow.notificationservice.presentation.slack.external;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.notificationservice.application.slack.SlackAppService;
import com.shipflow.notificationservice.application.slack.dto.command.SearchSlackMessageCommand;
import com.shipflow.notificationservice.domain.slack.type.SlackMessageType;
import com.shipflow.notificationservice.domain.slack.type.SlackSendStatus;
import com.shipflow.notificationservice.presentation.common.BasePageResponse;
import com.shipflow.notificationservice.presentation.slack.dto.request.SendSlackMessageRequest;
import com.shipflow.notificationservice.presentation.slack.dto.request.UpdateSlackMessageRequest;
import com.shipflow.notificationservice.presentation.slack.dto.response.SlackMessageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/slack")
public class SlackController {

	private final SlackAppService slackAppService;

	public SlackController(SlackAppService slackAppService) {
		this.slackAppService = slackAppService;
	}

	@PostMapping
	public ApiResponse<SlackMessageResponse> sendSlackMessage(
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole,
		@Valid @RequestBody SendSlackMessageRequest request) {
		return ApiResponse.ok(
			SlackMessageResponse.from(
				slackAppService.sendSlackMessage(
					request.toCommand(UUID.fromString(userId), userRole)
				)
			)
		);
	}

	@GetMapping("/{slackId}")
	public ApiResponse<SlackMessageResponse> getSlackMessage(
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole,
		@PathVariable UUID slackId) {
		return ApiResponse.ok(
			SlackMessageResponse.from(
				slackAppService.getSlackMessage(
					UUID.fromString(userId),
					userRole,
					slackId
				)
			)
		);
	}

	@GetMapping
	public ApiResponse<BasePageResponse<SlackMessageResponse>> getSlackMessages(
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole,
		@RequestParam(required = false) String receiverSlackId,
		@RequestParam(required = false) SlackSendStatus sendStatus,
		@RequestParam(required = false) SlackMessageType messageType,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtFrom,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtTo,
		Pageable pageable
	) {
		Page<SlackMessageResponse> page = slackAppService.getSlackMessages(
				new SearchSlackMessageCommand(
					UUID.fromString(userId),
					userRole,
					receiverSlackId,
					sendStatus,
					messageType,
					createdAtFrom,
					createdAtTo
				),
				pageable
			)
			.map(SlackMessageResponse::from);

		return ApiResponse.ok(BasePageResponse.from(page));
	}

	@PatchMapping("/{slackId}")
	public ApiResponse<SlackMessageResponse> updateSlackMessage(
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole,
		@PathVariable UUID slackId,
		@Valid @RequestBody UpdateSlackMessageRequest request
	) {
		return ApiResponse.ok(
			SlackMessageResponse.from(
				slackAppService.updateSlackMessage(
					request.toCommand(UUID.fromString(userId), userRole, slackId)
				)
			)
		);
	}

	@DeleteMapping("/{slackId}")
	public ApiResponse<Void> deleteSlackMessage(
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole,
		@PathVariable UUID slackId
	) {
		slackAppService.deleteSlackMessage(
			UUID.fromString(userId),
			userRole,
			slackId
		);
		return ApiResponse.ok(null);
	}
}