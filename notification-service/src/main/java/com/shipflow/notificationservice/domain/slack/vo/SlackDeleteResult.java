package com.shipflow.notificationservice.domain.slack.vo;

public record SlackDeleteResult(
	String slackTs,
	String slackChannelId
) {
}