package com.shipflow.userservice.presentation.dto.response;

import java.util.UUID;

import com.shipflow.userservice.application.dto.PatchSignupRequestResult;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;

import lombok.Getter;

@Getter
public class PatchSignupRequestResDto {
	private UUID userId;
	private UserStatus status;
	private UserRole role;

	public PatchSignupRequestResDto(PatchSignupRequestResult result) {
		this.userId = result.getUserId();
		this.status = result.getStatus();
		this.role = result.getRole();
	}
}
