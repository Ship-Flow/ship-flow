package com.shipflow.orderservice.application.service;

import com.shipflow.orderservice.domain.event.*;
import com.shipflow.orderservice.domain.exception.OrderNotFoundException;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.domain.model.OrderStatus;
import com.shipflow.orderservice.domain.repository.OrderReadModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderProjectionService {

    private final OrderReadModelRepository readModelRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderCreatingEvent e) {
        readModelRepository.save(OrderReadModel.builder()
                .orderId(e.orderId())
                .orderStatus(OrderStatus.CREATING)
                .ordererId(e.ordererId())
                .productId(e.productId())
                .supplierCompanyId(e.supplierCompanyId())
                .receiverCompanyId(e.receiverCompanyId())
                .departureHubId(e.departureHubId())
                .arrivalHubId(e.arrivalHubId())
                .quantity(e.quantity())
                .requestDeadline(e.requestDeadline())
                .requestNote(e.requestNote())
                .createdBy(e.createdBy())
                .createdAt(e.createdAt())
                .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderConfirmedEvent e) {
        readModelRepository.save(find(e.orderId()).toBuilder()
                .orderStatus(OrderStatus.CREATED)
                .productName(e.productName())
                .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderShipmentLinkedEvent e) {
        readModelRepository.save(find(e.orderId()).toBuilder()
                .shipmentId(e.shipmentId())
                .shipmentStatus(e.shipmentStatus())
                .departureHubId(e.departureHubId())
                .departureHubName(e.departureHubName())
                .arrivalHubId(e.arrivalHubId())
                .arrivalHubName(e.arrivalHubName())
                .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderCompletedEvent e) {
        readModelRepository.save(find(e.orderId()).toBuilder()
                .orderStatus(OrderStatus.COMPLETED)
                .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderCanceledProjectionEvent e) {
        readModelRepository.save(find(e.orderId()).toBuilder()
                .orderStatus(OrderStatus.CANCELED)
                .cancelReason(e.cancelReason())
                .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderFailedEvent e) {
        readModelRepository.save(find(e.orderId()).toBuilder()
                .orderStatus(OrderStatus.FAILED)
                .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderDeletedEvent e) {
        readModelRepository.save(find(e.orderId()).toBuilder()
                .deletedBy(e.deletedBy())
                .deletedAt(e.deletedAt())
                .build());
    }

    private OrderReadModel find(UUID orderId) {
        return readModelRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
