package com.shipflow.notificationservice.domain.slack.vo;

public record SlackDeleteInfo(
	String slackTs,
	String slackChannelId
) {
}