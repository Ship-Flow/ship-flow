package com.shipflow.orderservice.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shipflow.orderservice.application.dto.OrderSearchCondition;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.application.service.OrderQueryService;
import com.shipflow.orderservice.domain.exception.OrderNotFoundException;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.domain.model.OrderStatus;
import com.shipflow.orderservice.domain.model.UserRole;
import com.shipflow.orderservice.fixture.OrderFixture;
import com.shipflow.orderservice.infrastructure.web.UserContext;
import com.shipflow.orderservice.presentation.controller.OrderController;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean OrderCommandService orderCommandService;
    @MockitoBean OrderQueryService orderQueryService;
    @MockitoBean UserContext userContext;

    private final UUID orderId = OrderFixture.ORDER_ID;
    private final UUID userId  = OrderFixture.USER_ID;

    // ─────────────────────────────────────────────
    // POST /api/orders
    // ─────────────────────────────────────────────

    @Test
    void createOrder_성공_201반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(orderCommandService.createOrder(any(), eq(userId)))
                .thenReturn(OrderFixture.orderResult(orderId));

        mockMvc.perform(post("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.createRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("CREATING"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void createOrder_필수필드누락_400반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        // productId 누락
        String invalidBody = """
                {"quantity":1,"requestDeadline":"2026-12-31T23:59:00"}
                """;

        mockMvc.perform(post("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_수량0이하_400반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        String invalidBody = """
                {"productId":"%s","quantity":0,"requestDeadline":"2026-12-31T23:59:00"}
                """.formatted(OrderFixture.PRODUCT_ID);

        mockMvc.perform(post("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────
    // GET /api/orders/{orderId}
    // ─────────────────────────────────────────────

    @Test
    void getOrder_성공_200반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        when(orderQueryService.getOrder(eq(orderId), eq(userId), eq(UserRole.MASTER)))
                .thenReturn(OrderFixture.orderResult(orderId));

        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "MASTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("CREATING"));
    }

    @Test
    void getOrder_없는ID_404반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        when(orderQueryService.getOrder(eq(orderId), any(), any()))
                .thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "MASTER"))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // GET /api/orders (검색/정렬/페이지네이션)
    // ─────────────────────────────────────────────

    @Test
    void getOrders_기본요청_200반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        OrderReadModel model = OrderFixture.orderReadModel(orderId);
        Slice<OrderReadModel> slice = new SliceImpl<>(
                List.of(model),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                false
        );
        when(orderQueryService.searchOrders(any(), any())).thenReturn(slice);

        mockMvc.perform(get("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "MASTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.content[0].orderStatus").value("CREATING"));
    }

    @Test
    void getOrders_상태필터CREATED_파라미터전달됨() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        Slice<OrderReadModel> slice = new SliceImpl<>(List.of(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")), false);
        ArgumentCaptor<OrderSearchCondition> conditionCaptor =
                ArgumentCaptor.forClass(OrderSearchCondition.class);
        when(orderQueryService.searchOrders(conditionCaptor.capture(), any())).thenReturn(slice);

        mockMvc.perform(get("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "MASTER")
                        .param("status", "CREATED"))
                .andExpect(status().isOk());

        assertThat(conditionCaptor.getValue().status()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void getOrders_COMPANY_MANAGER_본인주문만조회() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.COMPANY_MANAGER);
        Slice<OrderReadModel> slice = new SliceImpl<>(List.of(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")), false);
        ArgumentCaptor<OrderSearchCondition> conditionCaptor =
                ArgumentCaptor.forClass(OrderSearchCondition.class);
        when(orderQueryService.searchOrders(conditionCaptor.capture(), any())).thenReturn(slice);

        mockMvc.perform(get("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "COMPANY_MANAGER"))
                .andExpect(status().isOk());

        assertThat(conditionCaptor.getValue().ordererId()).isEqualTo(userId);
    }

    @Test
    void getOrders_페이지네이션_파라미터전달됨() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        Slice<OrderReadModel> slice = new SliceImpl<>(List.of(),
                PageRequest.of(1, 30, Sort.by(Sort.Direction.DESC, "createdAt")), false);
        when(orderQueryService.searchOrders(any(), any())).thenReturn(slice);

        mockMvc.perform(get("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "MASTER")
                        .param("page", "1").param("size", "30"))
                .andExpect(status().isOk());
    }

    // ─────────────────────────────────────────────
    // PATCH /api/orders/{orderId}
    // ─────────────────────────────────────────────

    @Test
    void updateOrder_성공_200반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        when(orderCommandService.updateOrder(eq(orderId), any(), eq(userId)))
                .thenReturn(OrderFixture.orderResult(orderId));

        mockMvc.perform(patch("/api/orders/{id}", orderId)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "MASTER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.updateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    void updateOrder_권한없음_403반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.COMPANY_MANAGER);

        mockMvc.perform(patch("/api/orders/{id}", orderId)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "COMPANY_MANAGER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.updateRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateOrder_없는ID_404반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        when(orderCommandService.updateOrder(eq(orderId), any(), any()))
                .thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(patch("/api/orders/{id}", orderId)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "MASTER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.updateRequest())))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // POST /api/orders/{orderId}/cancel
    // ─────────────────────────────────────────────

    @Test
    void cancelOrder_성공_200반환() throws Exception {
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        doNothing().when(orderCommandService).cancelOrder(eq(orderId), any());

        mockMvc.perform(post("/api/orders/{id}/cancel", orderId)
                        .header("X-User-Role", "MASTER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.cancelRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void cancelOrder_권한없음_403반환() throws Exception {
        when(userContext.getUserRole(any())).thenReturn(UserRole.COMPANY_MANAGER);

        mockMvc.perform(post("/api/orders/{id}/cancel", orderId)
                        .header("X-User-Role", "COMPANY_MANAGER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.cancelRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelOrder_사유없음_400반환() throws Exception {
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);

        mockMvc.perform(post("/api/orders/{id}/cancel", orderId)
                        .header("X-User-Role", "MASTER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────
    // DELETE /api/orders/{orderId}
    // ─────────────────────────────────────────────

    @Test
    void deleteOrder_성공_204반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        doNothing().when(orderCommandService).deleteOrder(eq(orderId), eq(userId));

        mockMvc.perform(delete("/api/orders/{id}", orderId)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "MASTER"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrder_권한없음_403반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.SHIPMENT_MANAGER);

        mockMvc.perform(delete("/api/orders/{id}", orderId)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "SHIPMENT_MANAGER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteOrder_없는ID_404반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(userContext.getUserRole(any())).thenReturn(UserRole.MASTER);
        doThrow(new OrderNotFoundException(orderId))
                .when(orderCommandService).deleteOrder(eq(orderId), any());

        mockMvc.perform(delete("/api/orders/{id}", orderId)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "MASTER"))
                .andExpect(status().isNotFound());
    }
}
