package com.shipflow.orderservice.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOrderCommand(
        UUID ordererId,
        String ordererName,
        UUID productId,
        String productName,
        UUID supplierCompanyId,
        String supplierCompanyName,
        UUID receiverCompanyId,
        String receiverCompanyName,
        UUID departureHubId,
        UUID arrivalHubId,
        int quantity,
        LocalDateTime requestDeadline,
        String requestNote
) {
}
