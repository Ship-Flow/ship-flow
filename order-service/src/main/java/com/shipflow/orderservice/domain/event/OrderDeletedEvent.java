package com.shipflow.orderservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderDeletedEvent(UUID orderId, UUID deletedBy, LocalDateTime deletedAt) {}
