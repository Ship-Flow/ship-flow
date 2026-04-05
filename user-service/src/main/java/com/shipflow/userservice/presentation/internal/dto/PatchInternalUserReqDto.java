package com.shipflow.userservice.presentation.internal.dto;

import java.util.UUID;

import lombok.Getter;

@Getter
public class PatchInternalUserReqDto {
	private UUID hubId;
	private UUID companyId;
	private UUID updateBy;
	private String updatedAt;
}
