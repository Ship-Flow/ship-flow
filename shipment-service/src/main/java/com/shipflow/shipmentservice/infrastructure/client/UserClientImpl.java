package com.shipflow.shipmentservice.infrastructure.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.shipflow.shipmentservice.application.client.UserClient;
import com.shipflow.shipmentservice.application.client.dto.UserInfo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

	private final UserFeignClient userFeignClient;

	@Override
	public UserInfo getUser(UUID userId) {
		return userFeignClient.getUser(userId);
	}
}
