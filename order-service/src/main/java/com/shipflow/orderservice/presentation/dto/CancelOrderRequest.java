package com.shipflow.orderservice.presentation.dto;

import com.shipflow.orderservice.application.dto.CancelOrderCommand;
import jakarta.validation.constraints.NotBlank;

public record CancelOrderRequest(
        @NotBlank String reason
) {
    public CancelOrderCommand toCommand() {
        return new CancelOrderCommand(reason);
    }
}
