package com.shipflow.notificationservice.infrastructure.client.slack.dto;

public record SlackSendResult(
	String slackTs,
	String slackChannelId
) {
}