package com.shipflow.orderservice.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.application.service.OrderQueryService;
import com.shipflow.orderservice.domain.exception.OrderNotFoundException;
import com.shipflow.orderservice.fixture.OrderFixture;
import com.shipflow.orderservice.infrastructure.web.UserContext;
import com.shipflow.orderservice.presentation.controller.OrderInternalController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderInternalController.class)
class OrderInternalControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean OrderCommandService orderCommandService;
    @MockitoBean OrderQueryService orderQueryService;
    @MockitoBean UserContext userContext;

    private final UUID orderId = OrderFixture.ORDER_ID;
    private final UUID userId  = OrderFixture.USER_ID;

    @Test
    void prepareOrder_성공_201반환() throws Exception {
        when(userContext.getUserId(any())).thenReturn(userId);
        when(orderCommandService.createOrder(any(), any()))
                .thenReturn(OrderFixture.orderResult(orderId));

        mockMvc.perform(post("/internal/orders/prepare")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.createRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    void confirmOrder_성공_200반환() throws Exception {
        doNothing().when(orderCommandService).confirmCreation(orderId);

        mockMvc.perform(patch("/internal/orders/{id}/confirm", orderId))
                .andExpect(status().isOk());
    }

    @Test
    void confirmOrder_없는ID_404반환() throws Exception {
        doThrow(new OrderNotFoundException(orderId))
                .when(orderCommandService).confirmCreation(orderId);

        mockMvc.perform(patch("/internal/orders/{id}/confirm", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void failOrder_성공_200반환() throws Exception {
        doNothing().when(orderCommandService).failOrder(orderId);

        mockMvc.perform(patch("/internal/orders/{id}/fail", orderId))
                .andExpect(status().isOk());
    }

    @Test
    void cancelOrder_성공_200반환() throws Exception {
        doNothing().when(orderCommandService).cancelOrder(eq(orderId), any());

        mockMvc.perform(patch("/internal/orders/{id}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.cancelRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void completeOrder_성공_200반환() throws Exception {
        doNothing().when(orderCommandService).completeOrder(orderId);

        mockMvc.perform(patch("/internal/orders/{id}/complete", orderId))
                .andExpect(status().isOk());
    }

    @Test
    void getOrder_성공_200반환() throws Exception {
        when(orderQueryService.getOrder(orderId))
                .thenReturn(OrderFixture.orderResult(orderId));

        mockMvc.perform(get("/internal/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    void getReadModel_없는ID_404반환() throws Exception {
        when(orderQueryService.getReadModel(orderId))
                .thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(get("/internal/orders/{id}/read-model", orderId))
                .andExpect(status().isNotFound());
    }
}
