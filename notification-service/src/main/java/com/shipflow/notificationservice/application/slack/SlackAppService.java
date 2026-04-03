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
import com.shipflow.notificationservice.domain.slack.SlackSender;
import com.shipflow.notificationservice.domain.slack.exception.SlackErrorCode;
import com.shipflow.notificationservice.domain.slack.repository.SlackMessageRepository;
import com.shipflow.notificationservice.domain.slack.vo.SlackSendResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SlackAppService {

	private final SlackMessageRepository slackMessageRepository;
	private final SlackSender slackSender;

	// 메시지 전송
	// TODO: 인증 적용 후 userId 받아 createdBy 처리
	@Transactional
	public SlackMessageResult sendSlackMessage(SendSlackMessageCommand command) {

		SlackMessage slackMessage = slackMessageRepository.save(
			new SlackMessage(
				command.receiverSlackId(),
				command.relatedShipmentId(),
				command.relatedAiLogId(),
				command.message(),
				command.messageType()
			)
		);

		try {
			SlackSendResult result = slackSender.sendMessage(
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
		SlackMessage slackMessage = slackMessageRepository.findByIdAndDeletedAtIsNull(slackId)
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		return SlackMessageResult.from(slackMessage);
	}

	// 목록 조회
	public List<SlackMessageResult> getSlackMessages() {
		return slackMessageRepository.findAllByDeletedAtIsNull()
			.stream()
			.map(SlackMessageResult::from)
			.toList();
	}

	//슬랙 메세지 수정
	// TODO: 인증 적용 후 userId 받아 updatedBy 처리
	@Transactional
	public SlackMessageResult updateSlackMessage(UpdateSlackMessageCommand command) {

		SlackMessage slackMessage = slackMessageRepository.findByIdAndDeletedAtIsNull(command.slackId())
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		slackMessage.validateUpdatable();

		slackSender.updateMessage(
			slackMessage.getSlackChannelId(),
			slackMessage.getSlackTs(),
			command.message()
		);

		slackMessage.updateMessage(command.message());

		return SlackMessageResult.from(slackMessage);
	}

	//슬랙 메세지 삭제
	// TODO: 인증 적용 후 userId 받아 deletedBy 처리
	@Transactional
	public void deleteSlackMessage(UUID slackId, UUID userId) {
		SlackMessage slackMessage = slackMessageRepository.findByIdAndDeletedAtIsNull(slackId)
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		// 도메인 검증을 먼저 수행한 후, 외부 Slack 삭제 API 호출
		slackMessage.validateDeletable();

		slackSender.deleteMessage(
			slackMessage.getSlackChannelId(),
			slackMessage.getSlackTs()
		);

		slackMessage.markDeleted(userId);
	}
}