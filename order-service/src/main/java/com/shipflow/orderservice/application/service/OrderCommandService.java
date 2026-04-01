package com.shipflow.orderservice.application.service;

import com.shipflow.orderservice.application.dto.CancelOrderCommand;
import com.shipflow.orderservice.application.dto.CreateOrderCommand;
import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.application.dto.UpdateOrderCommand;
import com.shipflow.common.messaging.publisher.EventPublisher;
import com.shipflow.orderservice.infrastructure.messaging.event.outbound.*;
import com.shipflow.orderservice.domain.exception.OrderNotFoundException;
import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.repository.OrderRepository;
import com.shipflow.orderservice.domain.vo.CompanyInfo;
import com.shipflow.orderservice.domain.vo.HubInfo;
import com.shipflow.orderservice.domain.vo.Quantity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    public OrderResult createOrder(CreateOrderCommand cmd, UUID requesterId) {
        Order order = Order.create(
                cmd.ordererId(),
                cmd.productId(),
                new CompanyInfo(cmd.supplierCompanyId(), cmd.receiverCompanyId()),
                new HubInfo(cmd.departureHubId(), cmd.arrivalHubId()),
                new Quantity(cmd.quantity()),
                cmd.requestDeadline(),
                cmd.requestNote(),
                requesterId
        );
        Order saved = orderRepository.save(order);

        eventPublisher.publish(
                new OrderCreationStartedEvent(saved.getId(), saved.getProductId(), saved.getQuantity().getValue())
        );

        return OrderResult.from(saved);
    }

    public void confirmCreation(UUID orderId) {
        Order order = findOrThrow(orderId);
        order.confirmCreation();
        Order saved = orderRepository.save(order);

        eventPublisher.publish(
                new OrderCreatedEvent(
                        saved.getId(),
                        saved.getCompanyInfo().getSupplierCompanyId(),
                        saved.getCompanyInfo().getReceiverCompanyId(),
                        saved.getProductId(),
                        saved.getQuantity().getValue(),
                        saved.getHubInfo().getDepartureHubId(),
                        saved.getHubInfo().getArrivalHubId(),
                        saved.getRequestDeadline()
                )
        );
    }

    public void failOrder(UUID orderId) {
        Order order = findOrThrow(orderId);
        order.fail();
        orderRepository.save(order);
    }

    public void cancelOrder(UUID orderId, CancelOrderCommand cmd) {
        Order order = findOrThrow(orderId);
        order.cancel(cmd.reason());
        Order saved = orderRepository.save(order);

        eventPublisher.publish(
                new OrderCanceledEvent(saved.getId(), saved.getProductId(), saved.getQuantity().getValue())
        );
    }

    public void completeOrder(UUID orderId) {
        Order order = findOrThrow(orderId);
        order.complete();
        orderRepository.save(order);
    }

    public void linkShipment(UUID orderId, UUID shipmentId) {
        Order order = findOrThrow(orderId);
        order.linkShipment(shipmentId);
        orderRepository.save(order);
    }

    public OrderResult updateOrder(UUID orderId, UpdateOrderCommand cmd, UUID requesterId) {
        Order order = findOrThrow(orderId);
        order.update(
                cmd.productId(),
                new CompanyInfo(cmd.supplierCompanyId(), cmd.receiverCompanyId()),
                new HubInfo(cmd.departureHubId(), cmd.arrivalHubId()),
                new Quantity(cmd.quantity()),
                cmd.requestDeadline(),
                cmd.requestNote(),
                requesterId
        );
        return OrderResult.from(orderRepository.save(order));
    }

    public void deleteOrder(UUID orderId, UUID deleterId) {
        Order order = findOrThrow(orderId);
        order.softDelete(deleterId);
        orderRepository.save(order);
    }

    private Order findOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
