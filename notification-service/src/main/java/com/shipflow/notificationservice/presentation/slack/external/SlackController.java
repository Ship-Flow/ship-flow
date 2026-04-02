package com.shipflow.notificationservice.presentation.slack.external;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.notificationservice.application.slack.SlackAppService;
import com.shipflow.notificationservice.presentation.slack.dto.request.SendSlackMessageRequest;
import com.shipflow.notificationservice.presentation.slack.dto.request.UpdateSlackMessageRequest;
import com.shipflow.notificationservice.presentation.slack.dto.response.SlackMessageResponse;

@RestController
@RequestMapping("/api/slack")
public class SlackController {
	// TODO: Gateway + Keycloak 연동 후 @AuthenticationPrincipal 적용
	// TODO: external API 권한 체크(@PreAuthorize) 추가
	// TODO: internal API 서비스 간 인증 방식 반영
	private final SlackAppService slackAppService;

	public SlackController(SlackAppService slackAppService) {
		this.slackAppService = slackAppService;
	}

	@PostMapping
	public ApiResponse<SlackMessageResponse> sendSlackMessage(@RequestBody SendSlackMessageRequest request) {
		return ApiResponse.ok(
			SlackMessageResponse.from(
				slackAppService.sendSlackMessage(request.toCommand())
			)
		);
	}

	@GetMapping("/{slackId}")
	public ApiResponse<SlackMessageResponse> getSlackMessage(@PathVariable UUID slackId) {
		return ApiResponse.ok(SlackMessageResponse.from(slackAppService.getSlackMessage(slackId)));
	}

	// TODO: 목록 조회 페이징 및 검색 처리 필요
	@GetMapping
	public ApiResponse<List<SlackMessageResponse>> getAllSlackMessages() {
		return ApiResponse.ok(SlackMessageResponse.from(slackAppService.getSlackMessages()));
	}

	@PatchMapping("/{slackId}")
	public ApiResponse<SlackMessageResponse> updateSlackMessage(
		@PathVariable UUID slackId,
		@RequestBody UpdateSlackMessageRequest request
	) {
		return ApiResponse.ok(
			SlackMessageResponse.from(
				slackAppService.updateSlackMessage(request.toCommand(slackId))
			)
		);
	}

	@DeleteMapping("/{slackId}")
	public ApiResponse<SlackMessageResponse> deleteSlackMessage(@PathVariable UUID slackId) {
		UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000"); // TODO: Security 적용 후 교체
		slackAppService.deleteSlackMessage(slackId, userId);
		return ApiResponse.ok(null);
	}
}