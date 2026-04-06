package com.shipflow.userservice.application.internal.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetInternalUserResult {
	private UUID userId;
	private String name;
	private String slackId;
	private UUID hubId;
	private UUID companyId;
}
