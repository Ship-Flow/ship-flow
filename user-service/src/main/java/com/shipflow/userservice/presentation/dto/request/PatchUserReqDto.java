package com.shipflow.userservice.presentation.dto.request;

import com.shipflow.userservice.domain.model.UserRole;

import lombok.Getter;

@Getter
public class PatchUserReqDto {
	private String name;
	private String slackId;
	private UserRole role;
}
