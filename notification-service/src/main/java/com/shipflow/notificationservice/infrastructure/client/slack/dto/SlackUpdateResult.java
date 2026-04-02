package com.shipflow.notificationservice.infrastructure.client.slack.dto;

public record SlackUpdateResult(
	String slackTs,
	String slackChannelId,
	String messages
) {
}
