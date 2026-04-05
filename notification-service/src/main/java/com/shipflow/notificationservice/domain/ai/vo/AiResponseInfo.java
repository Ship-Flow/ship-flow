package com.shipflow.notificationservice.domain.ai.vo;

import java.time.LocalDateTime;

public record AiResponseInfo(
	String responseText,
	LocalDateTime finalDeadlineAt
) {
}