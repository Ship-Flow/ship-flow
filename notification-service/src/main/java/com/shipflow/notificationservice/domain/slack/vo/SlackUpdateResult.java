package com.shipflow.notificationservice.domain.slack.vo;

public record SlackUpdateResult(
	String slackTs,
	String slackChannelId,
	String message
) {
}
