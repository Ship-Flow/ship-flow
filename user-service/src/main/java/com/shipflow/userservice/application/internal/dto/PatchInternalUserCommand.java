package com.shipflow.userservice.application.internal.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchInternalUserCommand {
	private UUID hubId;
	private UUID companyId;
	private boolean hubIdUpdated;
	private boolean companyIdUpdated;
}