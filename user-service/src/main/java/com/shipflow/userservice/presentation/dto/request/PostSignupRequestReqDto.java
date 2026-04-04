package com.shipflow.userservice.presentation.dto.request;

import lombok.Getter;

@Getter
public class PostSignupRequestReqDto {
	private String username;
	private String password;
	private String name;
	private String slackId;
}
