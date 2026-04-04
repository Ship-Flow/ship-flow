package com.shipflow.notificationservice.domain.ai.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.shipflow.notificationservice.domain.ai.type.AiRequestType;

public record AiRequestInfo(
	//이벤트
	String fromHub,
	String toHub,
	List<String> route,
	String product,
	String requestNote,
	LocalDateTime deadline,
	String workingHours,
	AiRequestType requestType,
	//도전기능
	LocalDate workDate
) {
}
