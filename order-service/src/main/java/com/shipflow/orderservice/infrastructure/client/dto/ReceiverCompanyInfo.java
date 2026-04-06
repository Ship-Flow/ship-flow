package com.shipflow.orderservice.infrastructure.client.dto;

import java.util.UUID;

public record ReceiverCompanyInfo(
        UUID companyId,
        String companyName,
        UUID hubId
) {}
