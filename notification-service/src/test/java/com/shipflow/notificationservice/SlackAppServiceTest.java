package com.shipflow.notificationservice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.application.slack.SlackAppService;
import com.shipflow.notificationservice.application.slack.dto.command.SendSlackMessageCommand;
import com.shipflow.notificationservice.application.slack.dto.command.UpdateSlackMessageCommand;
import com.shipflow.notificationservice.application.slack.dto.result.SlackMessageResult;
import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.SlackMessageRepository;
import com.shipflow.notificationservice.domain.slack.SlackMessageType;
import com.shipflow.notificationservice.domain.slack.SlackSendStatus;
import com.shipflow.notificationservice.domain.slack.SlackSender;
import com.shipflow.notificationservice.domain.slack.exception.SlackErrorCode;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackDeleteResult;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackSendResult;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackUpdateResult;

@ExtendWith(MockitoExtension.class)
class SlackAppServiceTest {

	@Mock
	private SlackMessageRepository slackMessageRepository;

	@Mock
	private SlackSender slackSender;

	@InjectMocks
	private SlackAppService slackAppService;

	@Nested
	@DisplayName("슬랙 메시지 발송")
	class SendSlackMessageTest {

		@Test
		@DisplayName("수동 발송에 성공하면 SUCCESS 상태로 저장")
		void sendSlackMessage_success() {
			// given
			UUID relatedShipmentId = UUID.randomUUID();
			UUID relatedAiLogId = UUID.randomUUID();

			SendSlackMessageCommand command = new SendSlackMessageCommand(
				"U0APZGV2NRH",
				relatedShipmentId,
				relatedAiLogId,
				"배송을 시작해주세요.",
				SlackMessageType.MANUAL
			);

			SlackSendResult sendResult = new SlackSendResult(
				"1742891400.123456",
				"C0AQ2G43EUD"
			);

			// save()는 전달받은 SlackMessage 그대로 반환 (실제 DB처럼 동작)
			when(slackMessageRepository.save(any(SlackMessage.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

			when(slackSender.sendMessage("U0APZGV2NRH", "배송을 시작해주세요."))
				.thenReturn(sendResult);

			// when
			SlackMessageResult result = slackAppService.sendSlackMessage(command);

			// then
			// save()는 전송 전에 한 번만 호출됨 (서비스 구조 상 save → sendMessage → markSuccess 순서)
			verify(slackMessageRepository).save(any(SlackMessage.class));
			verify(slackSender).sendMessage("U0APZGV2NRH", "배송을 시작해주세요.");

			// SlackMessageResult에 markSuccess() 결과가 반영되어 있어야 함
			assertThat(result.receiverSlackId()).isEqualTo("U0APZGV2NRH");
			assertThat(result.relatedShipmentId()).isEqualTo(relatedShipmentId);
			assertThat(result.relatedAiLogId()).isEqualTo(relatedAiLogId);
			assertThat(result.message()).isEqualTo("배송을 시작해주세요.");
			assertThat(result.messageType()).isEqualTo(SlackMessageType.MANUAL);
			assertThat(result.sendStatus()).isEqualTo(SlackSendStatus.SUCCESS);
			assertThat(result.slackTs()).isEqualTo("1742891400.123456");
			assertThat(result.slackChannelId()).isEqualTo("C0AQ2G43EUD");
			assertThat(result.sentAt()).isNotNull();
		}

		@Test
		@DisplayName("슬랙 전송에 실패하면 FAIL 상태로 저장")
		void sendSlackMessage_fail() {
			// given
			UUID relatedShipmentId = UUID.randomUUID();
			UUID relatedAiLogId = UUID.randomUUID();

			SendSlackMessageCommand command = new SendSlackMessageCommand(
				"U0APZGV2NRH",
				relatedShipmentId,
				relatedAiLogId,
				"배송을 시작해주세요.",
				SlackMessageType.MANUAL
			);

			when(slackMessageRepository.save(any(SlackMessage.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

			when(slackSender.sendMessage("U0APZGV2NRH", "배송을 시작해주세요."))
				.thenThrow(new BusinessException(SlackErrorCode.SLACK_SEND_FAILED));

			// when
			SlackMessageResult result = slackAppService.sendSlackMessage(command);

			// then
			verify(slackMessageRepository).save(any(SlackMessage.class));
			verify(slackSender).sendMessage("U0APZGV2NRH", "배송을 시작해주세요.");

			assertThat(result.receiverSlackId()).isEqualTo("U0APZGV2NRH");
			assertThat(result.sendStatus()).isEqualTo(SlackSendStatus.FAIL);
			assertThat(result.slackTs()).isNull();
			assertThat(result.slackChannelId()).isNull();
			assertThat(result.sentAt()).isNull();
		}
	}

	@Nested
	@DisplayName("슬랙 메시지 단건 조회")
	class GetSlackMessageTest {

		@Test
		@DisplayName("존재하는 메시지 단건 조회 성공")
		void getSlackMessage_success() {
			// given
			UUID slackId = UUID.randomUUID();

			SlackMessage slackMessage = new SlackMessage(
				"U0APZGV2NRH",
				UUID.randomUUID(),
				UUID.randomUUID(),
				"조회 테스트 메시지",
				SlackMessageType.MANUAL
			);
			slackMessage.markSuccess("1742891400.123456", "C0AQ2G43EUD");

			when(slackMessageRepository.findByIdAndDeletedAtIsNull(slackId))
				.thenReturn(Optional.of(slackMessage));

			// when
			SlackMessageResult result = slackAppService.getSlackMessage(slackId);

			// then
			assertThat(result.receiverSlackId()).isEqualTo("U0APZGV2NRH");
			assertThat(result.message()).isEqualTo("조회 테스트 메시지");
			assertThat(result.sendStatus()).isEqualTo(SlackSendStatus.SUCCESS);
		}

		@Test
		@DisplayName("존재하지 않는 메시지 조회 시 BusinessException 발생")
		void getSlackMessage_notFound() {
			// given
			UUID slackId = UUID.randomUUID();

			when(slackMessageRepository.findByIdAndDeletedAtIsNull(slackId))
				.thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> slackAppService.getSlackMessage(slackId))
				.isInstanceOf(BusinessException.class);
		}
	}

	@Nested
	@DisplayName("슬랙 메시지 목록 조회")
	class GetSlackMessagesTest {

		@Test
		@DisplayName("삭제되지 않은 메시지 목록 전체 조회")
		void getSlackMessages_success() {
			// given
			SlackMessage first = new SlackMessage(
				"U0APZGV2NRH",
				UUID.randomUUID(),
				UUID.randomUUID(),
				"첫 번째 메시지",
				SlackMessageType.MANUAL
			);
			first.markSuccess("111.111", "C111");

			SlackMessage second = new SlackMessage(
				"C0AQ2G43EUD",
				UUID.randomUUID(),
				UUID.randomUUID(),
				"두 번째 메시지",
				SlackMessageType.DEADLINE_ALERT
			);
			second.markFail();

			when(slackMessageRepository.findAllByDeletedAtIsNull())
				.thenReturn(List.of(first, second));

			// when
			List<SlackMessageResult> results = slackAppService.getSlackMessages();

			// then
			assertThat(results).hasSize(2);
			assertThat(results.get(0).message()).isEqualTo("첫 번째 메시지");
			assertThat(results.get(0).sendStatus()).isEqualTo(SlackSendStatus.SUCCESS);
			assertThat(results.get(1).message()).isEqualTo("두 번째 메시지");
			assertThat(results.get(1).sendStatus()).isEqualTo(SlackSendStatus.FAIL);
		}
	}

	@Nested
	@DisplayName("슬랙 메시지 수정")
	class UpdateSlackMessageTest {

		@Test
		@DisplayName("SUCCESS 상태의 메시지는 수정 가능")
		void updateSlackMessage_success() {
			// given
			UUID slackId = UUID.randomUUID();

			UpdateSlackMessageCommand command = new UpdateSlackMessageCommand(
				slackId,
				"수정된 메시지"
			);

			SlackMessage slackMessage = new SlackMessage(
				"U0APZGV2NRH",
				UUID.randomUUID(),
				UUID.randomUUID(),
				"기존 메시지",
				SlackMessageType.MANUAL
			);
			slackMessage.markSuccess("1742891400.123456", "C0AQ2G43EUD");

			when(slackMessageRepository.findByIdAndDeletedAtIsNull(slackId))
				.thenReturn(Optional.of(slackMessage));

			when(slackSender.updateMessage("C0AQ2G43EUD", "1742891400.123456", "수정된 메시지"))
				.thenReturn(new SlackUpdateResult(
					"1742891400.123456",
					"C0AQ2G43EUD",
					"수정된 메시지"
				));

			// when
			SlackMessageResult result = slackAppService.updateSlackMessage(command);

			// then
			verify(slackSender).updateMessage("C0AQ2G43EUD", "1742891400.123456", "수정된 메시지");
			assertThat(slackMessage.getMessage()).isEqualTo("수정된 메시지");
			assertThat(result.message()).isEqualTo("수정된 메시지");
		}

		@Test
		@DisplayName("존재하지 않는 메시지 수정 시 BusinessException 발생")
		void updateSlackMessage_notFound() {
			// given
			UUID slackId = UUID.randomUUID();

			UpdateSlackMessageCommand command = new UpdateSlackMessageCommand(
				slackId,
				"수정된 메시지"
			);

			when(slackMessageRepository.findByIdAndDeletedAtIsNull(slackId))
				.thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> slackAppService.updateSlackMessage(command))
				.isInstanceOf(BusinessException.class);

			verify(slackSender, never()).updateMessage(any(), any(), any());
		}

		@Test
		@DisplayName("아직 발송되지 않은(slackTs 없는) 메시지는 수정 불가")
		void updateSlackMessage_notSent() {
			// given
			UUID slackId = UUID.randomUUID();

			UpdateSlackMessageCommand command = new UpdateSlackMessageCommand(
				slackId,
				"수정된 메시지"
			);

			// PENDING 상태: slackTs, slackChannelId 모두 null → 서비스에서 UPDATE_FAILED 예외
			SlackMessage slackMessage = new SlackMessage(
				"U0APZGV2NRH",
				UUID.randomUUID(),
				UUID.randomUUID(),
				"기존 메시지",
				SlackMessageType.MANUAL
			);

			when(slackMessageRepository.findByIdAndDeletedAtIsNull(slackId))
				.thenReturn(Optional.of(slackMessage));

			// when & then
			assertThatThrownBy(() -> slackAppService.updateSlackMessage(command))
				.isInstanceOf(BusinessException.class);

			verify(slackSender, never()).updateMessage(any(), any(), any());
		}
	}

	@Nested
	@DisplayName("슬랙 메시지 삭제")
	class DeleteSlackMessageTest {

		@Test
		@DisplayName("SUCCESS 상태의 메시지는 삭제 가능")
		void deleteSlackMessage_success() {
			// given
			UUID slackId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			SlackMessage slackMessage = new SlackMessage(
				"U0APZGV2NRH",
				UUID.randomUUID(),
				UUID.randomUUID(),
				"삭제할 메시지",
				SlackMessageType.MANUAL
			);
			slackMessage.markSuccess("1742891400.123456", "C0AQ2G43EUD");

			when(slackMessageRepository.findByIdAndDeletedAtIsNull(slackId))
				.thenReturn(Optional.of(slackMessage));

			when(slackSender.deleteMessage("C0AQ2G43EUD", "1742891400.123456"))
				.thenReturn(new SlackDeleteResult(
					"1742891400.123456",
					"C0AQ2G43EUD"
				));

			// when
			slackAppService.deleteSlackMessage(slackId, userId);

			// then
			verify(slackSender).deleteMessage("C0AQ2G43EUD", "1742891400.123456");
			assertThat(slackMessage.getDeletedAt()).isNotNull();
			assertThat(slackMessage.getDeletedBy()).isEqualTo(userId);
		}

		@Test
		@DisplayName("존재하지 않는 메시지 삭제 시 BusinessException 발생")
		void deleteSlackMessage_notFound() {
			// given
			UUID slackId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			when(slackMessageRepository.findByIdAndDeletedAtIsNull(slackId))
				.thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> slackAppService.deleteSlackMessage(slackId, userId))
				.isInstanceOf(BusinessException.class);

			verify(slackSender, never()).deleteMessage(any(), any());
		}

		@Test
		@DisplayName("FAIL 상태의 메시지는 삭제 불가 (validateDeletable 내부 검증)")
		void deleteSlackMessage_failStatus() {
			// given
			UUID slackId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			SlackMessage slackMessage = new SlackMessage(
				"U0APZGV2NRH",
				UUID.randomUUID(),
				UUID.randomUUID(),
				"발송 실패한 메시지",
				SlackMessageType.MANUAL
			);
			slackMessage.markFail();

			when(slackMessageRepository.findByIdAndDeletedAtIsNull(slackId))
				.thenReturn(Optional.of(slackMessage));

			// slackChannelId, slackTs가 null이므로 deleteMessage 호출 전에 markDeleted에서 예외 발생
			// 단, 서비스에서 slackSender.deleteMessage()를 먼저 호출하므로
			// null channelId/ts로 deleteMessage가 호출될 수 있음 → 실제 동작 확인 후 조정
			when(slackSender.deleteMessage(isNull(), isNull()))
				.thenReturn(new SlackDeleteResult(null, null));

			// when & then
			assertThatThrownBy(() -> slackAppService.deleteSlackMessage(slackId, userId))
				.isInstanceOf(BusinessException.class);
		}
	}
}
