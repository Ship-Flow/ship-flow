package com.shipflow.notificationservice.application.slack;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.SlackMessageType;
import com.shipflow.notificationservice.domain.slack.exception.SlackErrorCode;
import com.shipflow.notificationservice.infrastructure.client.slack.SlackApiClient;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackSendResult;
import com.shipflow.notificationservice.infrastructure.persistence.slack.SlackMessageRepository;
import com.shipflow.notificationservice.presentation.slack.dto.SlackMessageResponse;

@Service
@Transactional(readOnly = true)
public class SlackAppService {

	private final SlackMessageRepository slackMessageRepository;
	private final SlackApiClient slackApiClient;

	public SlackAppService(
		SlackMessageRepository slackMessageRepository,
		SlackApiClient slackApiClient
	) {
		this.slackMessageRepository = slackMessageRepository;
		this.slackApiClient = slackApiClient;
	}

	// 메시지 전송
	@Transactional
	public SlackMessage sendSlackMessage(
		String receiverSlackId,
		UUID relatedShipmentId,
		UUID relatedAiLogId,
		String message,
		SlackMessageType messageType
	) {
		SlackMessage slackMessage = slackMessageRepository.save(
			new SlackMessage(
				receiverSlackId,
				relatedShipmentId,
				relatedAiLogId,
				message,
				messageType
			)
		);

		try {
			SlackSendResult result = slackApiClient.sendMessage(receiverSlackId, message);
			slackMessage.markSuccess(result.slackTs(), result.slackChannelId());
		} catch (BusinessException e) {
			slackMessage.markFail();
		}

		return slackMessage;
	}

	// 단건 조회
	@Transactional
	public SlackMessageResponse getSlackMessage(UUID slackId) {
		SlackMessage slackMessage = slackMessageRepository.findByIdAndDeletedAtIsNull(slackId)
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		return SlackMessageResponse.from(slackMessage);
	}

	// 목록 조회
	@Transactional
	public List<SlackMessageResponse> getSlackMessages() {
		return slackMessageRepository.findAllByDeletedAtIsNull()
			.stream()
			.map(SlackMessageResponse::from)
			.toList();
	}

	/*
	 * TODO : 수정 및 삭제 처리 필요
	 */
}