package com.shipflow.notificationservice.domain.slack.vo;

public record SlackUpdateInfo(
	String slackTs,
	String slackChannelId,
	String message
) {
}
