package com.shipflow.orderservice.presentation.controller;

import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.application.service.OrderQueryService;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.infrastructure.web.UserContext;
import com.shipflow.orderservice.presentation.dto.CancelOrderRequest;
import com.shipflow.orderservice.presentation.dto.CreateOrderRequest;
import com.shipflow.orderservice.presentation.dto.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;
    private final UserContext userContext;

    @PostMapping("/prepare")
    public ResponseEntity<OrderResponse> prepareOrder(
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID requesterId = userContext.getUserId(httpRequest);
        OrderResult result = orderCommandService.createOrder(request.toCommand(), requesterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(result));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable UUID id) {
        orderCommandService.confirmCreation(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/fail")
    public ResponseEntity<Void> failOrder(@PathVariable UUID id) {
        orderCommandService.failOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID id,
            @Valid @RequestBody CancelOrderRequest request
    ) {
        orderCommandService.cancelOrder(id, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeOrder(@PathVariable UUID id) {
        orderCommandService.completeOrder(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(OrderResponse.from(orderQueryService.getOrder(id)));
    }

    @GetMapping("/{id}/read-model")
    public ResponseEntity<OrderReadModel> getReadModel(@PathVariable UUID id) {
        return ResponseEntity.ok(orderQueryService.getReadModel(id));
    }
}
