package com.shipflow.notificationservice.domain.ai;

import com.shipflow.notificationservice.domain.ai.vo.AiResponseInfo;

public interface AiGenerator {
	AiResponseInfo generate(String prompt);
}