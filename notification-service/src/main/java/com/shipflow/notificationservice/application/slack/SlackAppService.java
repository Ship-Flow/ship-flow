package com.shipflow.notificationservice.application.slack;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.application.slack.dto.command.SearchSlackMessageCommand;
import com.shipflow.notificationservice.application.slack.dto.command.SendSlackMessageCommand;
import com.shipflow.notificationservice.application.slack.dto.command.UpdateSlackMessageCommand;
import com.shipflow.notificationservice.application.slack.dto.result.SlackMessageResult;
import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.SlackSender;
import com.shipflow.notificationservice.domain.slack.exception.SlackErrorCode;
import com.shipflow.notificationservice.domain.slack.repository.SlackMessageRepository;
import com.shipflow.notificationservice.domain.slack.vo.SlackSendInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SlackAppService {

	private final SlackMessageRepository slackMessageRepository;
	private final SlackSender slackSender;

	// 메시지 전송
	// TODO: 자동 발송의 경우 receiverSlackId를 사용자/이벤트 정보 기반으로 조회하도록 분리
	@Transactional
	public SlackMessageResult sendSlackMessage(SendSlackMessageCommand command) {
		//권한 확인
		validateCreateRole(command.userRole());

		SlackMessage slackMessage = new SlackMessage(
			command.receiverSlackId(),
			command.relatedShipmentId(),
			command.relatedAiLogId(),
			command.message(),
			command.messageType()
		);
		slackMessage.markCreatedBy(command.userId());

		slackMessage = slackMessageRepository.save(slackMessage);

		try {
			SlackSendInfo result = slackSender.sendMessage(
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
	public SlackMessageResult getSlackMessage(UUID userId, String userRole, UUID slackId) {
		//권한 확인
		validateMasterRole(userRole);
		SlackMessage slackMessage = slackMessageRepository.findByIdAndDeletedAtIsNull(slackId)
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		return SlackMessageResult.from(slackMessage);
	}

	// 목록 조회
	public Page<SlackMessageResult> getSlackMessages(
		SearchSlackMessageCommand command,
		Pageable pageable
	) {
		//권한 확인
		validateMasterRole(command.userRole());

		return slackMessageRepository.search(command, pageable)
			.map(SlackMessageResult::from);
	}

	//슬랙 메세지 수정
	@Transactional
	public SlackMessageResult updateSlackMessage(UpdateSlackMessageCommand command) {
		//권한 확인
		validateMasterRole(command.userRole());

		SlackMessage slackMessage = slackMessageRepository.findByIdAndDeletedAtIsNull(command.slackId())
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		slackMessage.validateUpdatable();

		slackSender.updateMessage(
			slackMessage.getSlackChannelId(),
			slackMessage.getSlackTs(),
			command.message()
		);

		slackMessage.updateMessage(command.message(), command.userId());

		return SlackMessageResult.from(slackMessage);
	}

	//슬랙 메세지 삭제
	@Transactional
	public void deleteSlackMessage(UUID userId, String userRole, UUID slackId) {
		//권한 확인
		validateMasterRole(userRole);

		SlackMessage slackMessage = slackMessageRepository.findByIdAndDeletedAtIsNull(slackId)
			.orElseThrow(() -> new BusinessException(SlackErrorCode.SLACK_MESSAGE_NOT_FOUND));

		slackMessage.validateDeletable();

		slackSender.deleteMessage(
			slackMessage.getSlackChannelId(),
			slackMessage.getSlackTs()
		);

		slackMessage.markDeleted(userId);
	}

	private void validateCreateRole(String userRole) {
		if (!Set.of("MASTER", "HUB_MANAGER", "DELIVERY_MANAGER", "COMPANY_MANAGER").contains(userRole)) {
			throw new BusinessException(SlackErrorCode.FORBIDDEN_SLACK_ACCESS);
		}
	}

	private void validateMasterRole(String userRole) {
		if (!"MASTER".equals(userRole)) {
			throw new BusinessException(SlackErrorCode.FORBIDDEN_SLACK_ACCESS);
		}
	}
}