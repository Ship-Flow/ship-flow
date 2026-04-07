package com.shipflow.companyservice.application.dto.response;

import java.util.UUID;

public record UserInfoResponse(
	boolean success,
	UserData data,
	Object error
) {
	public record UserData(UUID id, String name, String slackId, UUID hubId, UUID companyId) {}
}
