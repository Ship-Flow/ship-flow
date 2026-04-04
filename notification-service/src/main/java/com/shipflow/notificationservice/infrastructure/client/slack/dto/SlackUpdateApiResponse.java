package com.shipflow.notificationservice.infrastructure.client.slack.dto;

public record SlackUpdateApiResponse(
	String slackTs,
	String slackChannelId,
	String message
) {
}
