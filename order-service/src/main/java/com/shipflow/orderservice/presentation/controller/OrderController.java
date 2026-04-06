package com.shipflow.orderservice.presentation.controller;

import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.application.service.OrderQueryService;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.infrastructure.web.UserContext;
import com.shipflow.orderservice.presentation.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        UUID ordererId = userContext.getUserId(httpRequest);
        OrderResult result = orderCommandService.createOrder(request, ordererId);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(result));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(OrderResponse.from(orderQueryService.getOrder(orderId)));
    }

    @GetMapping
    public ResponseEntity<Slice<OrderReadModelResponse>> getOrders(
            @ModelAttribute OrderSearchRequest searchRequest,
            @PageableDefault(size = 10, page = 0, sort = "createdAt",
                             direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Slice<OrderReadModel> result = orderQueryService.searchOrders(
                searchRequest.toCondition(), pageable
        );
        return ResponseEntity.ok(result.map(OrderReadModelResponse::from));
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
