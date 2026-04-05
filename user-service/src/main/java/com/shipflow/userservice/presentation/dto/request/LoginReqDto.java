package com.shipflow.userservice.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LoginReqDto {
	private String username;
	private String password;
}
