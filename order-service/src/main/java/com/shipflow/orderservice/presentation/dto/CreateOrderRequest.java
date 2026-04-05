package com.shipflow.orderservice.presentation.dto;

import com.shipflow.orderservice.application.dto.CreateOrderCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID ordererId,
        @NotNull UUID productId,
        @NotNull UUID supplierCompanyId,
        @NotNull UUID receiverCompanyId,
        @NotNull UUID departureHubId,
        @NotNull UUID arrivalHubId,
        @Min(1) int quantity,
        @NotNull LocalDateTime requestDeadline,
        String requestNote
) {
    public CreateOrderCommand toCommand() {
        return new CreateOrderCommand(
                ordererId, productId,
                supplierCompanyId, receiverCompanyId,
                departureHubId, arrivalHubId,
                quantity, requestDeadline, requestNote
        );
    }
}
