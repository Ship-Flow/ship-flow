package com.shipflow.userservice.application.dto;

import java.util.UUID;

import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;

import lombok.Getter;

@Getter
public class GetUsersResult {
	private UUID userId;
	private String userName;
	private String name;
	private String slackId;
	private UserRole role;
	private UUID hubId;
	private UUID companyId;
	private UserStatus status;
	private String createdAt;

	public GetUsersResult(User user) {
		this.userId = user.getId();
		this.userName = user.getName();
		this.name = user.getName();
		this.slackId = user.getSlackId();
		this.role = user.getRole();
		this.hubId = user.getHubId();
		this.companyId = user.getCompanyId();
		this.status = user.getStatus();
		this.createdAt = user.getCreatedAt().toString();
	}
}
