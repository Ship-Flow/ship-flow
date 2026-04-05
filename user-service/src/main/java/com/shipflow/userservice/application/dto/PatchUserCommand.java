package com.shipflow.userservice.application.dto;

import com.shipflow.userservice.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchUserCommand {
	private String name;
	private String slackId;
	private UserRole role;
}
