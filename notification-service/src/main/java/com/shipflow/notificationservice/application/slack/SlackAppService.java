package com.shipflow.notificationservice.application.slack;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.application.slack.dto.command.SendSlackMessageCommand;
import com.shipflow.notificationservice.application.slack.dto.command.UpdateSlackMessageCommand;
import com.shipflow.notificationservice.application.slack.dto.result.SlackMessageResult;
import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.exception.SlackErrorCode;
import com.shipflow.notificationservice.infrastructure.client.slack.SlackApiClient;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackSendResult;
import com.shipflow.notificationservice.infrastructure.persistence.slack.SlackMessageJpaRepository;

@Service
@Transactional(readOnly = true)
public class SlackAppService {

	private final SlackMessageJpaRepository slackMessageJpaRepository;
	private final SlackApiClient slackApiClient;

	public SlackAppService(
		SlackMessageJpaRepository slackMessageJpaRepository,
		SlackApiClient slackApiClient
	) {
		this.slackMessageJpaRepository = slackMessageJpaRepository;
		this.slackApiClient = slackApiClient;
	}

	// 메시지 전송
	@Transactional
	public SlackMessageResult sendSlackMessage(SendSlackMessageCommand command) {

		SlackMessage slackMessage = slackMessageJpaRepository.save(
			new SlackMessage(
				command.receiverSlackId(),
				command.relatedShipmentId(),
				command.relatedAiLogId(),
				command.message(),
				command.messageType()
			)
		);

		try {
			SlackSendResult result = slackApiClient.sendMessage(
				command.receiverSlackId(),
				command.message()
			);
			slackMessage.markSuccess(result.slackTs(), result.slackChannelId());
		} catch (BusinessException e) {
			slackMessage.markFail();
		}

		return SlackMessageResult.from(slackMessage);
	}

	// 단건 조회
	public SlackMessageResult getSlackMessage(UUID slackId) {
		SlackMessage slackMessage = slackMessageJpaRepository.findByIdAndDeletedAtIsNull(slackId)
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		return SlackMessageResult.from(slackMessage);
	}

	// 목록 조회
	public List<SlackMessageResult> getSlackMessages() {
		return slackMessageJpaRepository.findAllByDeletedAtIsNull()
			.stream()
			.map(SlackMessageResult::from)
			.toList();
	}

	//슬랙 메세지 수정
	@Transactional
	public SlackMessageResult updateSlackMessage(UpdateSlackMessageCommand command) {

		SlackMessage slackMessage = slackMessageJpaRepository.findByIdAndDeletedAtIsNull(command.slackId())
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		// 이미 발송된 메시지만 수정 가능 (선택)
		if (slackMessage.getSlackTs() == null || slackMessage.getSlackChannelId() == null) {
			throw new BusinessException(SlackErrorCode.SLACK_MESSAGE_UPDATE_FAILED);
		}

		slackApiClient.updateMessage(
			slackMessage.getSlackChannelId(),
			slackMessage.getSlackTs(),
			command.message()
		);

		slackMessage.updateMessage(command.message());

		return SlackMessageResult.from(slackMessage);
	}

	//슬랙 메세지 삭제
	@Transactional
	public void deleteSlackMessage(UUID slackId, UUID userId) {
		SlackMessage slackMessage = slackMessageJpaRepository.findByIdAndDeletedAtIsNull(slackId)
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		slackApiClient.deleteMessage(
			slackMessage.getSlackChannelId(),
			slackMessage.getSlackTs()
		);

		slackMessage.markDeleted(userId);

	}

}