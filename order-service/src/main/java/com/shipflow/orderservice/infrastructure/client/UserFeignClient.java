package com.shipflow.orderservice.infrastructure.client;

import com.shipflow.orderservice.infrastructure.client.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "userservice")
public interface UserFeignClient {

    @GetMapping("/internal/users/{userId}")
    UserInfo getUserInfo(
            @RequestHeader("X-Internal-Request") String internalRequest,
            @PathVariable("userId") UUID userId
    );
}
