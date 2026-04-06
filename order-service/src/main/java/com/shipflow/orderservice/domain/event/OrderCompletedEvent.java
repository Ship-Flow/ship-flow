package com.shipflow.orderservice.domain.event;

import java.util.UUID;

public record OrderCompletedEvent(UUID orderId) {}
