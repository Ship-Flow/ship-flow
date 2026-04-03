package com.shipflow.notificationservice.infrastructure.client.slack;

import java.io.IOException;
import java.util.Collections;

import org.springframework.stereotype.Component;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.domain.slack.SlackSender;
import com.shipflow.notificationservice.domain.slack.exception.SlackErrorCode;
import com.shipflow.notificationservice.domain.slack.vo.SlackDeleteInfo;
import com.shipflow.notificationservice.domain.slack.vo.SlackSendInfo;
import com.shipflow.notificationservice.domain.slack.vo.SlackUpdateInfo;
import com.shipflow.notificationservice.infrastructure.client.slack.config.SlackProperties;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatDeleteResponse;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.chat.ChatUpdateResponse;
import com.slack.api.methods.response.conversations.ConversationsOpenResponse;

@Component
public class SlackApiClient implements SlackSender {

	private final Slack slack;
	private final SlackProperties slackProperties;

	public SlackApiClient(Slack slack, SlackProperties slackProperties) {
		this.slack = slack;
		this.slackProperties = slackProperties;
	}

	//슬랙 메세지 발송 진입 메서드
	@Override
	public SlackSendInfo sendMessage(String receiverSlackId, String message) {
		try {
			validateReceiverSlackId(receiverSlackId);
			validateMessage(message);

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

	//메세지(개별 DM)
	private SlackSendInfo sendDirectMessage(String userSlackId, String message)
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

		return new SlackSendInfo(
			response.getTs(),
			response.getChannel()
		);
	}

	//메세지(채널)
	private SlackSendInfo sendChannelMessage(String channelId, String message)
		throws IOException, SlackApiException {

		ChatPostMessageResponse response = slack.methods(slackProperties.getBotToken())
			.chatPostMessage(req -> req
				.channel(channelId)
				.text(message)
			);

		if (!response.isOk()) {
			throw new BusinessException(SlackErrorCode.SLACK_SEND_FAILED);
		}

		return new SlackSendInfo(
			response.getTs(),
			response.getChannel()
		);
	}

	//슬랙 메세지 수정
	@Override
	public SlackUpdateInfo updateMessage(String channelId, String ts, String message) {
		try {
			validateSlackChannelId(channelId);
			validateSlackTs(ts);
			validateMessage(message);

			ChatUpdateResponse response = slack.methods(slackProperties.getBotToken())
				.chatUpdate(req -> req
					.channel(channelId)
					.ts(ts)
					.text(message)
				);

			if (!response.isOk()) {
				throw new BusinessException(SlackErrorCode.SLACK_MESSAGE_UPDATE_FAILED);
			}

			return new SlackUpdateInfo(
				response.getTs(),
				response.getChannel(),
				response.getText()
			);

		} catch (IOException | SlackApiException e) {
			throw new BusinessException(SlackErrorCode.SLACK_MESSAGE_UPDATE_FAILED);
		}
	}

	//슬랙 메세지 삭제
	@Override
	public SlackDeleteInfo deleteMessage(String channelId, String ts) {
		try {
			validateSlackChannelId(channelId);
			validateSlackTs(ts);

			ChatDeleteResponse response = slack.methods(slackProperties.getBotToken())
				.chatDelete(req -> req
					.channel(channelId)
					.ts(ts)
				);

			if (!response.isOk()) {
				throw new BusinessException(SlackErrorCode.SLACK_MESSAGE_DELETE_FAILED);
			}

			return new SlackDeleteInfo(
				response.getTs(),
				response.getChannel()
			);

		} catch (IOException | SlackApiException e) {
			throw new BusinessException(SlackErrorCode.SLACK_MESSAGE_DELETE_FAILED);
		}
	}

	//유효성 검증
	private void validateReceiverSlackId(String receiverSlackId) {
		if (receiverSlackId == null || receiverSlackId.isBlank()) {
			throw new BusinessException(SlackErrorCode.RECEIVER_SLACK_ID_REQUIRED);
		}
	}

	private void validateSlackChannelId(String slackChannelId) {
		if (slackChannelId == null || slackChannelId.isBlank()) {
			throw new BusinessException(SlackErrorCode.SLACK_CHANNEL_ID_REQUIRED);
		}
	}

	private void validateSlackTs(String slackTs) {
		if (slackTs == null || slackTs.isBlank()) {
			throw new BusinessException(SlackErrorCode.SLACK_TS_REQUIRED);
		}
	}

	private void validateMessage(String message) {
		if (message == null || message.isBlank()) {
			throw new BusinessException(SlackErrorCode.SLACK_MESSAGE_REQUIRED);
		}
	}

	//SlackID 유형별로 처리
	private boolean isUserId(String receiverSlackId) {
		return receiverSlackId.startsWith("U");
	}

	private boolean isChannelId(String receiverSlackId) {
		return receiverSlackId.startsWith("C")
			|| receiverSlackId.startsWith("D")
			|| receiverSlackId.startsWith("G");
	}
}