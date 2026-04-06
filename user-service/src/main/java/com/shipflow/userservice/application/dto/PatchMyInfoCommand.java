package com.shipflow.userservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchMyInfoCommand {
	private String name;
	private String slack;
}
