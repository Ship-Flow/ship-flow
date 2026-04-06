package com.shipflow.userservice.presentation.dto.response;

import java.util.UUID;

import com.shipflow.userservice.application.dto.GetUsersResult;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;

import lombok.Getter;

@Getter
public class GetUserResDto {
	private UUID userId;
	private String userName;
	private String name;
	private String slackId;
	private UserRole role;
	private UUID hubId;
	private UUID companyId;
	private UserStatus status;
	private String createdAt;

	public GetUserResDto(GetUsersResult result) {
		this.userId = result.getUserId();
		this.userName = result.getUserName();
		this.name = result.getUserName();
		this.slackId = result.getSlackId();
		this.role = result.getRole();
		this.hubId = result.getHubId();
		this.companyId = result.getCompanyId();
		this.status = result.getStatus();
		this.createdAt = result.getCreatedAt();
	}
}
