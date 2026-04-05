package com.shipflow.orderservice.domain.event;

import java.util.UUID;

public record OrderCanceledProjectionEvent(UUID orderId, String cancelReason) {}
