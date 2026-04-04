package com.shipflow.userservice.presentation.dto.request;

import lombok.Getter;

@Getter
public class PatchMyInfoReqDto {
	private String name;
	private String slackId;
}
