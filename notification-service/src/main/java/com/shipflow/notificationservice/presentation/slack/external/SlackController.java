package com.shipflow.notificationservice.presentation.slack.external;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}