package com.shipflow.notificationservice.domain.ai.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.shipflow.notificationservice.domain.ai.type.AiRequestType;

public class AiRequestInfo {
	//이벤트
	private String formHub;
	private String toHub;
	private List<String> route;
	private String product;
	private String requestNote;
	private LocalDateTime deadline;
	private String workingHours;

	private AiRequestType requestType;
	//도전기능
	private LocalDate workDate;

}
