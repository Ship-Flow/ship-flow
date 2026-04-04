package com.shipflow.notificationservice.domain.ai;

import com.shipflow.notificationservice.domain.ai.vo.AiRequestInfo;
import com.shipflow.notificationservice.domain.ai.vo.AiResponseInfo;

public interface AiGenerator {

	AiResponseInfo generate(AiRequestInfo requestInfo);
}
