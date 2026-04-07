package com.shipflow.shipmentservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.shipmentservice.application.client.dto.UserInfo;

@FeignClient(name = "userservice", path = "/internal/users")
public interface UserFeignClient {

	@GetMapping("/{userId}")
	ApiResponse<UserInfo> getUser(@PathVariable("userId") UUID userId);
}
