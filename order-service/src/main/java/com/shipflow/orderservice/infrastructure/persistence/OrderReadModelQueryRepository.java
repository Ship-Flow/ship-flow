package com.shipflow.orderservice.infrastructure.persistence;

import com.shipflow.orderservice.application.dto.OrderSearchCondition;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface OrderReadModelQueryRepository {
    Slice<OrderReadModel> search(OrderSearchCondition condition, Pageable pageable);
}
