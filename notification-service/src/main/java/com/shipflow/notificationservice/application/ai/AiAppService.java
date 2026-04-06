package com.shipflow.notificationservice.application.ai;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.application.ai.dto.command.GenerateDeadlineCommand;
import com.shipflow.notificationservice.application.ai.dto.command.SearchAiLogCommand;
import com.shipflow.notificationservice.application.ai.dto.result.AiLogResult;
import com.shipflow.notificationservice.domain.ai.AiGenerator;
import com.shipflow.notificationservice.domain.ai.AiLog;
import com.shipflow.notificationservice.domain.ai.exception.AiErrorCode;
import com.shipflow.notificationservice.domain.ai.repository.AiLogRepository;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;
import com.shipflow.notificationservice.domain.ai.vo.AiResponseInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AiAppService {

	private static final String DEFAULT_WORKING_HOURS = "09:00 ~ 18:00";

	private final AiLogRepository aiLogRepository;
	private final AiGenerator aiGenerator;

	//테스트용 외부(AI만 실행)
	@Transactional
	public AiLogResult generateAiLog(GenerateDeadlineCommand command) {
		validateCommand(command);

		String prompt = createDeadlinePrompt(command);

		AiLog aiLog = new AiLog(
			command.relatedShipmentId(),
			command.shipmentManagerId(),
			prompt,
			command.requestType()
		);
		aiLog.markCreatedBy(command.ordererId());

		aiLog = aiLogRepository.save(aiLog);

		try {
			AiResponseInfo result = aiGenerator.generate(prompt);

			aiLog.markSuccess(
				result.responseText(),
				result.finalDeadlineAt()
			);

			return AiLogResult.from(aiLog);

		} catch (BusinessException e) {
			aiLog.markFail();
			throw e;
		} catch (Exception e) {
			aiLog.markFail();
			throw new BusinessException(AiErrorCode.AI_GENERATE_FAILED);
		}
	}

	//단건조회
	public AiLogResult getAiLog(UUID userId, String userRole, UUID aiId) {
		validateMasterRole(userRole);
		AiLog aiLog = aiLogRepository.findByIdAndDeletedAtIsNull(aiId)
			.orElseThrow(() -> new BusinessException(AiErrorCode.AI_LOG_NOT_FOUND));

		return AiLogResult.from(aiLog);
	}

	//목록조회
	public Page<AiLogResult> getAiLogs(
		SearchAiLogCommand command,
		Pageable pageable
	) {
		validateMasterRole(command.userRole());

		return aiLogRepository.search(command, pageable)
			.map(AiLogResult::from);
	}

	private void validateCommand(GenerateDeadlineCommand command) {
		if (command == null) {
			throw new BusinessException(AiErrorCode.AI_EVENT_NOT_FOUND);
		}
		if (command.requestType() == null) {
			throw new BusinessException(AiErrorCode.AI_REQUEST_TYPE_REQUIRED);
		}
		if (command.requestType() != AiRequestType.DEADLINE) {
			throw new BusinessException(AiErrorCode.AI_EVENT_INVALID);
		}
		if (command.fromHub() == null || command.fromHub().isBlank()) {
			throw new BusinessException(AiErrorCode.AI_FROM_HUB_REQUIRED);
		}
		if (command.toHub() == null || command.toHub().isBlank()) {
			throw new BusinessException(AiErrorCode.AI_TO_HUB_REQUIRED);
		}
		if (command.product() == null || command.product().isBlank()) {
			throw new BusinessException(AiErrorCode.AI_PRODUCT_REQUIRED);
		}
		if (command.deadline() == null) {
			throw new BusinessException(AiErrorCode.AI_DEADLINE_REQUIRED);
		}
	}

	// AI 요청용 프롬프트 (AI 입력)
	private String createDeadlinePrompt(GenerateDeadlineCommand command) {
		String routeText = (command.route() == null || command.route().isEmpty())
			? "없음"
			: String.join(", ", command.route());

		String requestNote = (command.requestNote() == null || command.requestNote().isBlank())
			? "없음"
			: command.requestNote();

		String workingHours = (command.workingHours() == null || command.workingHours().isBlank())
			? DEFAULT_WORKING_HOURS
			: command.workingHours();

		return """
			다음 물류 정보를 바탕으로 최종 발송 시한을 계산해라.
			
			발송지: %s
			경유지: %s
			도착지: %s
			상품: %s
			요청사항: %s
			납기: %s
			근무시간: %s
			
			반드시 ISO-8601 형식의 발송 시한만 포함해서 응답해라.
			예시: 2026-04-04T09:00:00
			""".formatted(
			command.fromHub(),
			routeText,
			command.toHub(),
			command.product(),
			requestNote,
			command.deadline(),
			workingHours
		);
	}

	private void validateMasterRole(String userRole) {
		if (!"MASTER".equals(userRole)) {
			throw new BusinessException(AiErrorCode.FORBIDDEN_AI_ACCESS);
		}
	}
}