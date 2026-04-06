package com.shipflow.userservice.presentation.internal.dto;

import java.util.UUID;

import com.shipflow.userservice.application.internal.dto.PatchInternalUserResult;

import lombok.Getter;

@Getter
public class PatchInternalUserResDto {
	private UUID userId;
	private UUID hubId;
	private UUID companyId;

	public PatchInternalUserResDto(PatchInternalUserResult result) {
		this.userId = result.getUserId();
		this.hubId = result.getHubId();
		this.companyId = result.getCompanyId();
	}
}
