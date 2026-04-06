package com.shipflow.shipmentservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shipflow.shipmentservice.application.client.dto.UserInfo;

@FeignClient(name = "user-service", path = "/internal/users")
public interface UserFeignClient {

	@GetMapping("/{userId}")
	UserInfo getUser(@PathVariable("userId") UUID userId);
}
