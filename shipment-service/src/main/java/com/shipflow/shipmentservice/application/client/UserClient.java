package com.shipflow.shipmentservice.application.client;

import java.util.UUID;

import com.shipflow.shipmentservice.application.client.dto.UserInfo;

public interface UserClient {

	UserInfo getUser(UUID userId);
}
