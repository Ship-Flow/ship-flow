package com.shipflow.notificationservice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import com.shipflow.notificationservice.application.ai.AiAppService;
import com.shipflow.notificationservice.application.ai.dto.command.GenerateDeadlineCommand;
import com.shipflow.notificationservice.application.ai.dto.result.AiLogResult;
import com.shipflow.notificationservice.domain.ai.AiGenerator;
import com.shipflow.notificationservice.domain.ai.AiLog;
import com.shipflow.notificationservice.domain.ai.exception.AiErrorCode;
import com.shipflow.notificationservice.domain.ai.repository.AiLogRepository;
import com.shipflow.notificationservice.domain.ai.type.AiRequestStatus;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;
import com.shipflow.notificationservice.domain.ai.vo.AiResponseInfo;

@ExtendWith(MockitoExtension.class)
class AiAppServiceTest {

	@Mock
	private AiLogRepository aiLogRepository;

	@Mock
	private AiGenerator aiGenerator;

	@InjectMocks
	private AiAppService aiAppService;

	@Test
	@DisplayName("AI 생성 성공")
	void generate_success() {
		GenerateDeadlineCommand command = validCommand();

		when(aiLogRepository.save(any()))
			.thenAnswer(invocation -> invocation.getArgument(0));

		AiResponseInfo response = mock(AiResponseInfo.class);
		when(response.responseText()).thenReturn("2026-04-10T09:00:00");
		when(response.finalDeadlineAt()).thenReturn(LocalDateTime.now());

		when(aiGenerator.generate(anyString()))
			.thenReturn(response);

		AiLogResult result = aiAppService.generateAiLog(command);

		assertThat(result.requestStatus()).isEqualTo(AiRequestStatus.SUCCESS);
		assertThat(result.responseText()).isEqualTo("2026-04-10T09:00:00");
	}

	@Test
	@DisplayName("AI 생성 실패")
	void generate_fail_business() {
		GenerateDeadlineCommand command = validCommand();

		when(aiLogRepository.save(any()))
			.thenAnswer(invocation -> invocation.getArgument(0));

		when(aiGenerator.generate(anyString()))
			.thenThrow(new BusinessException(AiErrorCode.AI_GENERATE_FAILED));

		assertThatThrownBy(() -> aiAppService.generateAiLog(command))
			.isInstanceOf(BusinessException.class);
	}

	//유효성 검증
	@Nested
	class ValidateTest {

		@Test
		void null_command() {
			assertThatThrownBy(() -> aiAppService.generateAiLog(null))
				.isInstanceOf(BusinessException.class);
		}

		@Test
		void requestType_null() {
			GenerateDeadlineCommand command = mock(GenerateDeadlineCommand.class);
			when(command.requestType()).thenReturn(null);

			assertThatThrownBy(() -> aiAppService.generateAiLog(command))
				.isInstanceOf(BusinessException.class);
		}

		@Test
		void fromHub_blank() {
			GenerateDeadlineCommand command = mock(GenerateDeadlineCommand.class);

			when(command.requestType()).thenReturn(AiRequestType.DEADLINE);
			when(command.fromHub()).thenReturn(" ");

			assertThatThrownBy(() -> aiAppService.generateAiLog(command))
				.isInstanceOf(BusinessException.class);
		}

		@Test
		void toHub_blank() {
			GenerateDeadlineCommand command = mock(GenerateDeadlineCommand.class);

			when(command.requestType()).thenReturn(AiRequestType.DEADLINE);
			when(command.fromHub()).thenReturn("정상");
			when(command.toHub()).thenReturn(" ");

			assertThatThrownBy(() -> aiAppService.generateAiLog(command))
				.isInstanceOf(BusinessException.class);
		}

		@Test
		void product_blank() {
			GenerateDeadlineCommand command = mock(GenerateDeadlineCommand.class);

			when(command.requestType()).thenReturn(AiRequestType.DEADLINE);
			when(command.fromHub()).thenReturn("정상");
			when(command.toHub()).thenReturn("정상");
			when(command.product()).thenReturn(" ");

			assertThatThrownBy(() -> aiAppService.generateAiLog(command))
				.isInstanceOf(BusinessException.class);
		}

		@Test
		void deadline_null() {
			GenerateDeadlineCommand command = mock(GenerateDeadlineCommand.class);

			when(command.requestType()).thenReturn(AiRequestType.DEADLINE);
			when(command.fromHub()).thenReturn("정상");
			when(command.toHub()).thenReturn("정상");
			when(command.product()).thenReturn("상품");
			when(command.deadline()).thenReturn(null);

			assertThatThrownBy(() -> aiAppService.generateAiLog(command))
				.isInstanceOf(BusinessException.class);
		}
	}

	// =========================
	// 조회
	// =========================

	@Test
	void get_success() {
		UUID id = UUID.randomUUID();

		AiLog aiLog = new AiLog(
			UUID.randomUUID(),
			UUID.randomUUID(),
			"prompt",
			AiRequestType.DEADLINE
		);

		aiLog.markSuccess("ok", LocalDateTime.now());

		when(aiLogRepository.findByIdAndDeletedAtIsNull(id))
			.thenReturn(Optional.of(aiLog));

		AiLogResult result = aiAppService.getAiLog(id);

		assertThat(result.requestStatus()).isEqualTo(AiRequestStatus.SUCCESS);
	}

	private GenerateDeadlineCommand validCommand() {
		GenerateDeadlineCommand command = mock(GenerateDeadlineCommand.class);

		when(command.relatedShipmentId()).thenReturn(UUID.randomUUID());
		when(command.shipmentManagerId()).thenReturn(UUID.randomUUID());
		when(command.requestType()).thenReturn(AiRequestType.DEADLINE);
		when(command.fromHub()).thenReturn("경기");
		when(command.toHub()).thenReturn("부산");
		when(command.product()).thenReturn("상품");
		when(command.deadline()).thenReturn(LocalDateTime.now());

		return command;
	}
}