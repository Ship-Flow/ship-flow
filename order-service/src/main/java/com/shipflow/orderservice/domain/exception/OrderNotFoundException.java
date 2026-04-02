package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.BusinessException;

import java.util.UUID;

public class OrderNotFoundException extends BusinessException {

    public OrderNotFoundException(UUID orderId) {
        super(OrderErrorCode.ORDER_NOT_FOUND, "주문을 찾을 수 없습니다: " + orderId);
    }
}
