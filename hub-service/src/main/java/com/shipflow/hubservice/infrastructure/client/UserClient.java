package com.shipflow.hubservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userservice")
public interface UserClient {

    @PatchMapping("/internal/users/{userId}")
    void updateUserHubAssignment(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("userId") UUID userId,
        @RequestBody UpdateUserHubAssignmentRequest request
    );

    record UpdateUserHubAssignmentRequest(
        UUID hubId,
        UUID companyId,
        String updatedAt
    ) {
    }
}
