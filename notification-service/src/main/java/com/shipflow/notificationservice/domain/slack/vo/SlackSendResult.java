package com.shipflow.notificationservice.domain.slack.vo;

public record SlackSendResult(
	String slackTs,
	String slackChannelId
) {
}