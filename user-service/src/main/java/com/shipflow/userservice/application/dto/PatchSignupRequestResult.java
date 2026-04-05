package com.shipflow.userservice.application.dto;

import java.util.UUID;

import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchSignupRequestResult {
	private UUID userId;
	private UserStatus status;
	private UserRole role;
}
