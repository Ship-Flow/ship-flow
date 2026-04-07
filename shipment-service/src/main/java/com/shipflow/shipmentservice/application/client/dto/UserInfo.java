package com.shipflow.shipmentservice.application.client.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

	@JsonProperty("id")
	private UUID userId;
	private String name;
	private String slackId;
}
