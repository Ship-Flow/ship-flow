package com.shipflow.orderservice.application.dto;

public record CancelOrderCommand(
        String reason
) {
}
