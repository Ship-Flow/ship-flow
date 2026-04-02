package com.shipflow.notificationservice.infrastructure.client.slack;

import java.io.IOException;
import java.util.Collections;

import org.springframework.stereotype.Component;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.domain.slack.exception.SlackErrorCode;
import com.shipflow.notificationservice.infrastructure.client.slack.config.SlackProperties;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackSendResult;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsOpenResponse;

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
			validateReceiverSlackId(receiverSlackId);

			if (isUserId(receiverSlackId)) {
				return sendDirectMessage(receiverSlackId, message);
			}

			if (isChannelId(receiverSlackId)) {
				return sendChannelMessage(receiverSlackId, message);
			}

			throw new BusinessException(SlackErrorCode.INVALID_SLACK_ID_FORMAT);

		} catch (IOException | SlackApiException e) {
			throw new BusinessException(SlackErrorCode.SLACK_SEND_FAILED);
		}
	}

	private SlackSendResult sendDirectMessage(String userSlackId, String message)
		throws IOException, SlackApiException {

		ConversationsOpenResponse openResponse = slack.methods(slackProperties.getBotToken())
			.conversationsOpen(req -> req.users(Collections.singletonList(userSlackId)));

		if (!openResponse.isOk()) {
			throw new BusinessException(SlackErrorCode.SLACK_SEND_FAILED);
		}

		String dmChannelId = openResponse.getChannel().getId();

		ChatPostMessageResponse response = slack.methods(slackProperties.getBotToken())
			.chatPostMessage(req -> req
				.channel(dmChannelId)
				.text(message)
			);

		if (!response.isOk()) {
			throw new BusinessException(SlackErrorCode.SLACK_SEND_FAILED);
		}

		return new SlackSendResult(
			response.getTs(),
			response.getChannel()
		);
	}

	private SlackSendResult sendChannelMessage(String channelId, String message)
		throws IOException, SlackApiException {

		ChatPostMessageResponse response = slack.methods(slackProperties.getBotToken())
			.chatPostMessage(req -> req
				.channel(channelId)
				.text(message)
			);

		if (!response.isOk()) {
			throw new BusinessException(SlackErrorCode.SLACK_SEND_FAILED);
		}

		return new SlackSendResult(
			response.getTs(),
			response.getChannel()
		);
	}

	private void validateReceiverSlackId(String receiverSlackId) {
		if (receiverSlackId == null || receiverSlackId.isBlank()) {
			throw new BusinessException(SlackErrorCode.RECEIVER_SLACK_ID_REQUIRED);
		}
	}

	private boolean isUserId(String receiverSlackId) {
		return receiverSlackId.startsWith("U");
	}

	private boolean isChannelId(String receiverSlackId) {
		return receiverSlackId.startsWith("C")
			|| receiverSlackId.startsWith("D")
			|| receiverSlackId.startsWith("G");
	}
}