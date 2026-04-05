package com.shipflow.shipmentservice.application.client.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfo {

	private UUID userId;
	private String name;
	private String slackId;
}
