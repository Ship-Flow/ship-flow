package com.shipflow.userservice.application.dto;

import java.util.UUID;

import com.shipflow.userservice.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchUserResult {
	private UUID id;
	private String name;
	private String slackId;
	private UserRole role;
}
