package com.shipflow.userservice.application.dto;

import java.util.List;
import java.util.UUID;

import com.shipflow.userservice.domain.model.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetSignupRequestResult {
	private List<SignupRequest> content;
	private int page;
	private int size;
	private long totalCount;

	@Getter
	@AllArgsConstructor
	public static class SignupRequest {
		private UUID id;
		private String username;
		private String name;
		private String slackId;
		private UserStatus status;
		private String createdAt;
	}
}
