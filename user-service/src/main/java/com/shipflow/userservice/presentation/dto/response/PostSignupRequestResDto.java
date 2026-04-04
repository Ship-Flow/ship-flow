package com.shipflow.userservice.presentation.dto.response;

import java.util.UUID;

import com.shipflow.userservice.application.dto.CreateSignupRequestResult;
import com.shipflow.userservice.domain.model.UserStatus;

import lombok.Getter;

@Getter
public class PostSignupRequestResDto {
	private UUID userId;
	private String userName;
	private String name;
	private String slackId;
	private UserStatus status;
	private String createdAt;

	public PostSignupRequestResDto(CreateSignupRequestResult result) {
		this.userId = result.getId();
		this.userName = result.getUserName();
		this.name = result.getName();
		this.slackId = result.getSlackId();
		this.status = result.getStatus();
		this.createdAt = result.getCreatedAt();

	}
}
