package com.shipflow.orderservice.presentation.controller;

import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.application.service.OrderQueryService;
import com.shipflow.orderservice.infrastructure.web.UserContext;
import com.shipflow.orderservice.presentation.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID requesterId = userContext.getUserId(httpRequest);
        OrderResult result = orderCommandService.createOrder(request.toCommand(), requesterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(result));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(OrderResponse.from(orderQueryService.getOrder(orderId)));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<OrderResponse> responses = orderQueryService.getOrders().stream()
                .map(OrderResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID requesterId = userContext.getUserId(httpRequest);
        OrderResult result = orderCommandService.updateOrder(orderId, request.toCommand(), requesterId);
        return ResponseEntity.ok(OrderResponse.from(result));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody CancelOrderRequest request
    ) {
        orderCommandService.cancelOrder(orderId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID orderId,
            HttpServletRequest httpRequest
    ) {
        UUID requesterId = userContext.getUserId(httpRequest);
        orderCommandService.deleteOrder(orderId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
