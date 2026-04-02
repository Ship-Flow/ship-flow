package com.shipflow.notificationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shipflow.notificationservice.application.slack.SlackAppService;
import com.shipflow.notificationservice.application.slack.dto.command.SendSlackMessageCommand;
import com.shipflow.notificationservice.application.slack.dto.result.SlackMessageResult;
import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.SlackMessageRepository;
import com.shipflow.notificationservice.domain.slack.SlackMessageType;
import com.shipflow.notificationservice.domain.slack.SlackSendStatus;
import com.shipflow.notificationservice.domain.slack.SlackSender;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackSendResult;

@ExtendWith(MockitoExtension.class)
class SlackAppServiceTest {

	@Mock
	private SlackMessageRepository slackMessageRepository;

	@Mock
	private SlackSender slackSender;

	@InjectMocks
	private SlackAppService slackAppService;

	//slack 수동발송
	@DisplayName("슬랙 메시지 수동 발송에 성공하면 SUCCESS 상태로 저장된다")
	@Test
	void sendSlackMessage_success() {
		// given
		UUID relatedShipmentId = UUID.randomUUID();
		UUID relatedAiLogId = UUID.randomUUID();

		SendSlackMessageCommand command = new SendSlackMessageCommand(
			"U12345678",
			relatedShipmentId,
			relatedAiLogId,
			"배송을 시작해주세요.",
			SlackMessageType.MANUAL
		);

		SlackSendResult sendResult = new SlackSendResult(
			"1742891400.123456",
			"C08ABCDE12"
		);

		when(slackMessageRepository.save(any(SlackMessage.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		when(slackSender.sendMessage("U12345678", "배송을 시작해주세요."))
			.thenReturn(sendResult);

		// when
		SlackMessageResult result = slackAppService.sendSlackMessage(command);

		// then
		ArgumentCaptor<SlackMessage> captor = ArgumentCaptor.forClass(SlackMessage.class);
		verify(slackMessageRepository).save(captor.capture());
		verify(slackSender).sendMessage("U12345678", "배송을 시작해주세요.");

		SlackMessage savedMessage = captor.getValue();

		assertThat(savedMessage.getReceiverSlackId()).isEqualTo("U12345678");
		assertThat(savedMessage.getRelatedShipmentId()).isEqualTo(relatedShipmentId);
		assertThat(savedMessage.getRelatedAiLogId()).isEqualTo(relatedAiLogId);
		assertThat(savedMessage.getMessage()).isEqualTo("배송을 시작해주세요.");
		assertThat(savedMessage.getMessageType()).isEqualTo(SlackMessageType.MANUAL);
		assertThat(savedMessage.getSendStatus()).isEqualTo(SlackSendStatus.SUCCESS);
		assertThat(savedMessage.getSlackTs()).isEqualTo("1742891400.123456");
		assertThat(savedMessage.getSlackChannelId()).isEqualTo("C08ABCDE12");
		assertThat(savedMessage.getSentAt()).isNotNull();

		assertThat(result.getReceiverSlackId()).isEqualTo("U12345678");
		assertThat(result.getMessage()).isEqualTo("배송을 시작해주세요.");
		assertThat(result.getMessageType()).isEqualTo(SlackMessageType.MANUAL);
		assertThat(result.getSendStatus()).isEqualTo(SlackSendStatus.SUCCESS);
		assertThat(result.getSlackTs()).isEqualTo("1742891400.123456");
		assertThat(result.getSlackChannelId()).isEqualTo("C08ABCDE12");
	}
}

	//slack 단건조회
	//slack 목록조회
	//slack 수정
	//slack 삭제

}
