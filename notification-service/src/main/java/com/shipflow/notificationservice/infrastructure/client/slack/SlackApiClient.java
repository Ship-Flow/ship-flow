package com.shipflow.notificationservice.infrastructure.client.slack;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.shipflow.notificationservice.infrastructure.client.slack.config.SlackProperties;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackSendResult;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

@Component
public class SlackApiClient {

	private final Slack slack;
	private final SlackProperties slackProperties;

	public SlackApiClient(Slack slack, SlackProperties slackProperties) {
		this.slack = slack;
		this.slackProperties = slackProperties;
	}
	
	// TODO: SlackErrorCode 적용 필요
	public SlackSendResult sendMessage(String receiverSlackId, String message) {
		try {
			ChatPostMessageResponse response = slack.methods(slackProperties.getBotToken())
				.chatPostMessage(req -> req
					.channel(receiverSlackId)
					.text(message)
				);

			if (!response.isOk()) {
				throw new RuntimeException("Slack API Error: " + response.getError());
			}

			return new SlackSendResult(
				response.getTs(),
				response.getChannel()
			);

		} catch (IOException | SlackApiException e) {
			throw new RuntimeException("Slack 메시지 전송 실패", e);
		}
	}
}