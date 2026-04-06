package com.shipflow.userservice.presentation.dto.response;

import java.util.UUID;

import com.shipflow.userservice.domain.model.UserRole;

import lombok.Getter;

@Getter
public class LoginResDto {
	private String accessToken;
	private Integer expiresIn;

	private  UUID userId;
	private String username;
	private UserRole role;
	private String slackId;
	private UUID hubId;
	private UUID companyId;

	public LoginResDto(
		String accessToken,
		Integer expiresIn,
		UUID userId,
		String username,
		UserRole role,
		String slackId,
		UUID hubId,
		UUID companyId
	) {
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
		this.userId = userId;
		this.username = username;
		this.role = role;
		this.slackId = slackId;
		this.hubId = hubId;
		this.companyId = companyId;
	}
}