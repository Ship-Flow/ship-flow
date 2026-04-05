package com.shipflow.userservice.presentation.dto.request;

import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;

import lombok.Getter;

@Getter
public class PatchSignupRequestReqDto {
	private UserStatus status;
	private UserRole role;
}