package com.shipflow.notificationservice.infrastructure.client.slack;

import org.springframework.stereotype.Component;

import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackSendResult;

@Component
public class SlackApiClient {

	public SlackSendResult sendMessage(String receiverSlackId, String message) {
		return new SlackSendResult("mock-ts", "mock-channel");
	}
}