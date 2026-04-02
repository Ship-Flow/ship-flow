package com.shipflow.orderservice.domain.event;

import java.util.UUID;

public record OrderConfirmedEvent(UUID orderId, String productName) {}
