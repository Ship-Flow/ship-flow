package com.shipflow.shipmentservice.application.client.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class UserInfo {

	private UUID userId;
	private String name;
	private String slackId;
}
