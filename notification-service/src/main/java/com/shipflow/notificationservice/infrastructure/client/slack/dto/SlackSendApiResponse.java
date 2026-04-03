package com.shipflow.notificationservice.infrastructure.client.slack.dto;

public record SlackSendApiResponse(
	String slackTs,
	String slackChannelId
) {
}