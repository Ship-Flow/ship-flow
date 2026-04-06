package com.shipflow.userservice.application.dto;

import java.util.UUID;

import com.shipflow.userservice.domain.model.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateSignupRequestResult {
	private UUID id;
	private String userName;
	private String name;
	private String slackId;
	private UserStatus status;
	private String createdAt;
}
