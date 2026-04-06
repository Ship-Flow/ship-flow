package com.shipflow.userservice.application.internal.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchInternalUserResult {
	private UUID userId;
	private UUID hubId;
	private UUID companyId;
}
