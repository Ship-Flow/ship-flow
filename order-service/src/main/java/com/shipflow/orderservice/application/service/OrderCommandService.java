package com.shipflow.orderservice.application.service;

import com.shipflow.orderservice.application.dto.CancelOrderCommand;
import com.shipflow.orderservice.application.dto.CreateOrderCommand;
import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.application.dto.UpdateOrderCommand;
import com.shipflow.orderservice.presentation.dto.CreateOrderRequest;
import com.shipflow.common.messaging.publisher.EventPublisher;
import com.shipflow.orderservice.domain.event.*;
import com.shipflow.orderservice.domain.model.ShipmentStatus;
import com.shipflow.orderservice.infrastructure.messaging.event.publish.*;
import com.shipflow.orderservice.domain.exception.OrderNotFoundException;
import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.repository.OrderRepository;
import com.shipflow.orderservice.domain.vo.CompanyInfo;
import com.shipflow.orderservice.domain.vo.HubInfo;
import com.shipflow.orderservice.domain.vo.Quantity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final EventPublisher rabbitPublisher;
    private final ApplicationEventPublisher domainEventPublisher;
    private final OrderFetchService orderFetchService;

    public OrderResult createOrder(CreateOrderRequest request, UUID ordererId) {
        CreateOrderCommand cmd = orderFetchService.fetchAndBuild(
                ordererId, request.productId(), request.quantity(),
                request.requestDeadline(), request.requestNote()
        );

        Order order = Order.create(
                cmd.ordererId(),
                cmd.productId(),
                new CompanyInfo(cmd.supplierCompanyId(), cmd.receiverCompanyId()),
                new HubInfo(cmd.departureHubId(), cmd.arrivalHubId()),
                new Quantity(cmd.quantity()),
                cmd.requestDeadline(),
                cmd.requestNote(),
                ordererId
        );
        Order saved = orderRepository.save(order);

        domainEventPublisher.publishEvent(new OrderCreatingEvent(
                saved.getId(), saved.getOrdererId(), cmd.ordererName(),
                saved.getProductId(), cmd.productName(),
                saved.getCompanyInfo().getSupplierCompanyId(), cmd.supplierCompanyName(),
                saved.getCompanyInfo().getReceiverCompanyId(), cmd.receiverCompanyName(),
                saved.getHubInfo().getDepartureHubId(),
                saved.getHubInfo().getArrivalHubId(),
                saved.getQuantity().getValue(),
                saved.getRequestDeadline(), saved.getRequestNote(),
                saved.getCreatedBy(), saved.getCreatedAt()
        ));

        rabbitPublisher.publish(
                new OrderCreationStartedEvent(saved.getId(), saved.getProductId(), saved.getQuantity().getValue())
        );

        return OrderResult.from(saved);
    }

    public void confirmCreation(UUID orderId, String productName) {
        Order order = findOrThrow(orderId);
        order.confirmCreation();
        Order saved = orderRepository.save(order);

        domainEventPublisher.publishEvent(new OrderConfirmedEvent(saved.getId(), productName));

        rabbitPublisher.publish(
                new OrderCreatedEvent(
                        saved.getId(),
                        saved.getOrdererId(),
                        saved.getCompanyInfo().getSupplierCompanyId(),
                        saved.getCompanyInfo().getReceiverCompanyId(),
                        saved.getProductId(),
                        saved.getQuantity().getValue(),
                        saved.getHubInfo().getDepartureHubId(),
                        saved.getHubInfo().getArrivalHubId(),
                        saved.getRequestDeadline(),
                        saved.getRequestNote()
                )
        );
    }

    public void failOrder(UUID orderId) {
        Order order = findOrThrow(orderId);
        order.fail();
        orderRepository.save(order);
        domainEventPublisher.publishEvent(new OrderFailedEvent(orderId));
    }

    public void failOrderByShipment(UUID orderId) {
        Order order = findOrThrow(orderId);
        order.fail();
        Order saved = orderRepository.save(order);
        domainEventPublisher.publishEvent(new OrderFailedEvent(orderId));
        rabbitPublisher.publish(
                new OrderCreationFailedEvent(
                        saved.getId(),
                        saved.getProductId(),
                        saved.getQuantity().getValue()
                )
        );
    }

    public void cancelOrder(UUID orderId, CancelOrderCommand cmd) {
        Order order = findOrThrow(orderId);
        order.cancel(cmd.reason());
        Order saved = orderRepository.save(order);

        domainEventPublisher.publishEvent(new OrderCanceledProjectionEvent(saved.getId(), cmd.reason()));

        rabbitPublisher.publish(
                new OrderCanceledEvent(saved.getId(), saved.getProductId(), saved.getQuantity().getValue())
        );
    }

    public void completeOrder(UUID orderId) {
        Order order = findOrThrow(orderId);
        order.complete();
        orderRepository.save(order);
        domainEventPublisher.publishEvent(new OrderCompletedEvent(orderId));
    }

    public void linkShipment(UUID orderId, UUID shipmentId,
                             ShipmentStatus shipmentStatus,
                             UUID departureHubId, String departureHubName,
                             UUID arrivalHubId, String arrivalHubName) {
        Order order = findOrThrow(orderId);
        order.linkShipment(shipmentId);
        orderRepository.save(order);
        domainEventPublisher.publishEvent(new OrderShipmentLinkedEvent(
                orderId, shipmentId, shipmentStatus,
                departureHubId, departureHubName,
                arrivalHubId, arrivalHubName
        ));
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
        Order saved = orderRepository.save(order);
        domainEventPublisher.publishEvent(new OrderUpdatedEvent(
                saved.getId(),
                saved.getProductId(),
                saved.getCompanyInfo().getSupplierCompanyId(),
                saved.getCompanyInfo().getReceiverCompanyId(),
                saved.getHubInfo().getDepartureHubId(),
                saved.getHubInfo().getArrivalHubId(),
                saved.getQuantity().getValue(),
                saved.getRequestDeadline(),
                saved.getRequestNote(),
                saved.getUpdatedBy(),
                saved.getUpdatedAt()
        ));
        return OrderResult.from(saved);
    }

    public void deleteOrder(UUID orderId, UUID deleterId) {
        Order order = findOrThrow(orderId);
        order.softDelete(deleterId);
        Order saved = orderRepository.save(order);
        domainEventPublisher.publishEvent(new OrderDeletedEvent(
                saved.getId(), saved.getDeletedBy(), saved.getDeletedAt()
        ));
    }

    private Order findOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
