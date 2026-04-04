package com.shipflow.notificationservice.domain.slack.vo;

public record SlackSendInfo(
	String slackTs,
	String slackChannelId
) {
}