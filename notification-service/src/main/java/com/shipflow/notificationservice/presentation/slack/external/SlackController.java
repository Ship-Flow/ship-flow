package com.shipflow.notificationservice.presentation.slack.external;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.notificationservice.application.slack.SlackAppService;
import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.presentation.slack.dto.SlackMessageResponse;
import com.shipflow.notificationservice.presentation.slack.dto.SlackSendRequest;

@RestController
@RequestMapping("/api/slack")
public class SlackController {

	private final SlackAppService slackAppService;

	public SlackController(SlackAppService slackAppService) {
		this.slackAppService = slackAppService;
	}

	@PostMapping
	public SlackMessageResponse sendSlackMessage(@RequestBody SlackSendRequest request) {
		SlackMessage slackMessage = slackAppService.sendSlackMessage(
			request.receiverSlackId(),
			request.relatedShipmentId(),
			request.relatedAiLogId(),
			request.message(),
			request.messageType()
		);

		return SlackMessageResponse.from(slackMessage);
	}

	@GetMapping("/{slackId}")
	public ApiResponse<SlackMessageResponse> getSlackMessage(@PathVariable UUID slackId) {
		return ApiResponse.ok(slackAppService.getSlackMessage(slackId));
	}

	// TODO: 목록 조회 페이징 및 검색 처리 필요
	@GetMapping
	public ApiResponse<List<SlackMessageResponse>> getAllSlackMessages() {
		return ApiResponse.ok(slackAppService.getSlackMessages());
	}
}