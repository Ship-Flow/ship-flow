package com.shipflow.orderservice.presentation.dto;

import com.shipflow.orderservice.application.dto.OrderSearchCondition;
import com.shipflow.orderservice.domain.model.OrderStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderSearchRequest(
        OrderStatus status,
        UUID ordererId,
        UUID productId,
        UUID supplierCompanyId,
        UUID receiverCompanyId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo
) {
    public OrderSearchCondition toCondition() {
        return new OrderSearchCondition(
                status, ordererId, productId,
                supplierCompanyId, receiverCompanyId,
                createdFrom, createdTo
        );
    }
}
