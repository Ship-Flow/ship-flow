package com.shipflow.notificationservice.infrastructure.client.slack.dto;

public record SlackDeleteResult(
	String slackTs,
	String slackChannelId
) {
}