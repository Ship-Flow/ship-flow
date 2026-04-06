package com.shipflow.orderservice.application.dto;

import com.shipflow.orderservice.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderSearchCondition(
        OrderStatus status,
        UUID ordererId,
        UUID productId,
        UUID supplierCompanyId,
        UUID receiverCompanyId,
        LocalDateTime createdFrom,
        LocalDateTime createdTo
) {}
