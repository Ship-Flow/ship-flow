package com.shipflow.notificationservice.application.slack;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.SlackMessageType;
import com.shipflow.notificationservice.infrastructure.client.slack.SlackApiClient;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackSendResult;
import com.shipflow.notificationservice.infrastructure.persistence.slack.SlackMessageRepository;

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

	//메세지 전송
	@Transactional
	public SlackMessage sendSlackMessage(
		String receiverSlackId,
		UUID relatedShipmentId,
		UUID relatedAiLogId,
		String message,
		SlackMessageType messageType
	) {
		SlackMessage slackMessage = new SlackMessage(
			receiverSlackId,
			relatedShipmentId,
			relatedAiLogId,
			message,
			messageType
		);

		slackMessageRepository.save(slackMessage);

		try {
			SlackSendResult result = slackApiClient.sendMessage(receiverSlackId, message);
			slackMessage.markSuccess(result.slackTs(), result.slackChannelId());
			return slackMessage;
		} catch (Exception e) {
			slackMessage.markFail();
			return slackMessage;
		}
	}

	//단건조회
	public SlackMessage getSlackMessage(UUID slackMessageId) {
		return slackMessageRepository.findByIdAndDeletedAtIsNull(slackMessageId)
			.orElseThrow(() -> new IllegalArgumentException("슬랙 메시지를 찾을 수 없습니다. id=" + slackMessageId));
	}

	//목록 조회
	public Page<SlackMessage> getSlackMessageList(Pageable pageable) {
		return slackMessageRepository.findAllByDeletedAtIsNull(pageable);
	}

	/*
	 * Todo : 수정 및 삭제 처리 필요
	 */

}
