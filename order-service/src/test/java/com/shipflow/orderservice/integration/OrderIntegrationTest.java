package com.shipflow.orderservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shipflow.orderservice.application.dto.CreateOrderCommand;
import com.shipflow.orderservice.application.service.OrderFetchService;
import com.shipflow.orderservice.fixture.OrderFixture;
import com.shipflow.orderservice.presentation.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderIntegrationTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JdbcTemplate jdbcTemplate;
    @MockitoBean OrderFetchService orderFetchService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM orders.p_order_read_models");
        jdbcTemplate.execute("DELETE FROM orders.p_orders");

        when(orderFetchService.fetchAndBuild(any(), any(), anyInt(), any(), any()))
                .thenReturn(new CreateOrderCommand(
                        OrderFixture.USER_ID, "주문자명",
                        OrderFixture.PRODUCT_ID, "상품명",
                        OrderFixture.SUPPLIER_ID, "공급사명",
                        OrderFixture.RECEIVER_ID, "수신사명",
                        OrderFixture.DEP_HUB_ID, OrderFixture.ARR_HUB_ID,
                        10, OrderFixture.DEADLINE, "테스트 메모"
                ));
    }

    @Test
    void 주문생성_조회_전체흐름() throws Exception {
        UUID userId = OrderFixture.USER_ID;

        // 1. 주문 생성
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.createRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATING"))
                .andReturn();

        // 2. 생성된 orderId 추출
        OrderResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), OrderResponse.class);
        UUID orderId = created.id();

        // 3. DB에 저장 확인
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders.p_orders WHERE id = ?::uuid", Integer.class, orderId.toString()
        )).isEqualTo(1);

        // 4. 단건 조회
        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void 주문취소_상태변경확인() throws Exception {
        UUID userId = OrderFixture.USER_ID;

        // 1. 주문 생성
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.createRequest())))
                .andExpect(status().isCreated())
                .andReturn();

        OrderResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), OrderResponse.class);
        UUID orderId = created.id();

        // 2. 취소
        mockMvc.perform(post("/api/orders/{id}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderFixture.cancelRequest())))
                .andExpect(status().isOk());

        // 3. 취소 상태 확인
        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.cancelReason").value("재고 부족"));
    }

    @Test
    void 없는주문조회_404반환() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/orders/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
