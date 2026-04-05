package com.shipflow.userservice.presentation.dto.response;

import java.util.UUID;

import com.shipflow.userservice.application.dto.PatchUserResult;
import com.shipflow.userservice.domain.model.UserRole;

import lombok.Getter;

@Getter
public class PatchUserResDto {
	private UUID id;
	private String name;
	private String slackId;
	private UserRole role;

	public PatchUserResDto(PatchUserResult result) {
		this.id = result.getId();
		this.name = result.getName();
		this.slackId = result.getSlackId();
		this.role = result.getRole();
	}
}
