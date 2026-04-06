package com.shipflow.orderservice.infrastructure.client.dto;

import java.util.UUID;

public record UserInfo(
        UUID userId,
        String ordererName,
        UUID receiverCompanyId
) {}
