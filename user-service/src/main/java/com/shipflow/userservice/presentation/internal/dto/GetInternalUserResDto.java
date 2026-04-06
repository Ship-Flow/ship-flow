package com.shipflow.userservice.presentation.internal.dto;

import java.util.UUID;

import com.shipflow.userservice.application.internal.dto.GetInternalUserResult;

import lombok.Getter;

@Getter
public class GetInternalUserResDto {
	private UUID id;
	private String name;
	private String slackId;
	private UUID hubId;
	private UUID companyId;

	public GetInternalUserResDto(GetInternalUserResult result) {
		this.id = result.getUserId();
		this.name = result.getName();
		this.slackId = result.getSlackId();
		this.hubId = result.getHubId();
		this.companyId = result.getCompanyId();
	}
}
