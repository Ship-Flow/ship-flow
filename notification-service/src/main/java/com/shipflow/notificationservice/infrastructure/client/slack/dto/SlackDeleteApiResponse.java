package com.shipflow.notificationservice.infrastructure.client.slack.dto;

public record SlackDeleteApiResponse(
	String slackTs,
	String slackChannelId
) {
}