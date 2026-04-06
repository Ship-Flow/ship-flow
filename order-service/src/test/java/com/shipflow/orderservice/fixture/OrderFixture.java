package com.shipflow.orderservice.fixture;

import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.domain.model.OrderStatus;
import com.shipflow.orderservice.domain.vo.CompanyInfo;
import com.shipflow.orderservice.domain.vo.HubInfo;
import com.shipflow.orderservice.domain.vo.Quantity;
import com.shipflow.orderservice.presentation.dto.CancelOrderRequest;
import com.shipflow.orderservice.presentation.dto.CreateOrderRequest;
import com.shipflow.orderservice.presentation.dto.UpdateOrderRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderFixture {

    public static final UUID ORDER_ID    = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID USER_ID     = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID PRODUCT_ID  = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final UUID SUPPLIER_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    public static final UUID RECEIVER_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    public static final UUID DEP_HUB_ID  = UUID.fromString("66666666-6666-6666-6666-666666666666");
    public static final UUID ARR_HUB_ID  = UUID.fromString("77777777-7777-7777-7777-777777777777");
    public static final LocalDateTime DEADLINE = LocalDateTime.of(2026, 12, 31, 23, 59);

    /** 컨트롤러/서비스 테스트용 OrderResult (DB 없이 생성) */
    public static OrderResult orderResult(UUID orderId) {
        return new OrderResult(
                orderId, USER_ID, PRODUCT_ID, null,
                SUPPLIER_ID, RECEIVER_ID, DEP_HUB_ID, ARR_HUB_ID,
                10, OrderStatus.CREATING, null,
                DEADLINE, "테스트 메모",
                USER_ID, LocalDateTime.of(2026, 4, 1, 9, 0),
                null, null
        );
    }

    /** 도메인 객체 직접 생성 (서비스 테스트용) */
    public static Order order(UUID orderId) {
        return Order.reconstruct(
                orderId, USER_ID, PRODUCT_ID, null,
                new CompanyInfo(SUPPLIER_ID, RECEIVER_ID),
                new HubInfo(DEP_HUB_ID, ARR_HUB_ID),
                new Quantity(10),
                OrderStatus.CREATING, null,
                DEADLINE, "테스트 메모",
                USER_ID, LocalDateTime.of(2026, 4, 1, 9, 0),
                null, null, null, null
        );
    }

    public static OrderReadModel orderReadModel(UUID orderId) {
        return OrderReadModel.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.CREATING)
                .ordererId(USER_ID)
                .productId(PRODUCT_ID)
                .supplierCompanyId(SUPPLIER_ID)
                .receiverCompanyId(RECEIVER_ID)
                .departureHubId(DEP_HUB_ID)
                .arrivalHubId(ARR_HUB_ID)
                .quantity(10)
                .requestDeadline(DEADLINE)
                .requestNote("테스트 메모")
                .createdBy(USER_ID)
                .createdAt(LocalDateTime.of(2026, 4, 1, 9, 0))
                .build();
    }

    /** CREATED 상태 도메인 객체 */
    public static Order createdOrder(UUID orderId) {
        return Order.reconstruct(
                orderId, USER_ID, PRODUCT_ID, null,
                new CompanyInfo(SUPPLIER_ID, RECEIVER_ID),
                new HubInfo(DEP_HUB_ID, ARR_HUB_ID),
                new Quantity(10),
                OrderStatus.CREATED, null,
                DEADLINE, "테스트 메모",
                USER_ID, LocalDateTime.of(2026, 4, 1, 9, 0),
                null, null, null, null
        );
    }

    public static OrderReadModel orderReadModelWithStatus(UUID orderId, OrderStatus status) {
        return OrderReadModel.builder()
                .orderId(orderId)
                .orderStatus(status)
                .ordererId(USER_ID)
                .productId(PRODUCT_ID)
                .supplierCompanyId(SUPPLIER_ID)
                .receiverCompanyId(RECEIVER_ID)
                .departureHubId(DEP_HUB_ID)
                .arrivalHubId(ARR_HUB_ID)
                .quantity(10)
                .requestDeadline(DEADLINE)
                .requestNote("테스트 메모")
                .createdBy(USER_ID)
                .createdAt(LocalDateTime.of(2026, 4, 1, 9, 0))
                .build();
    }

    public static OrderReadModel createdOrderReadModel(UUID orderId) {
        return OrderReadModel.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.CREATED)
                .ordererId(USER_ID)
                .productId(PRODUCT_ID)
                .supplierCompanyId(SUPPLIER_ID)
                .receiverCompanyId(RECEIVER_ID)
                .departureHubId(DEP_HUB_ID)
                .arrivalHubId(ARR_HUB_ID)
                .quantity(10)
                .requestDeadline(DEADLINE)
                .requestNote("테스트 메모")
                .createdBy(USER_ID)
                .createdAt(LocalDateTime.of(2026, 4, 1, 9, 0))
                .build();
    }

    public static CreateOrderRequest createRequest() {
        return new CreateOrderRequest(
                PRODUCT_ID, 10, DEADLINE, "테스트 메모"
        );
    }

    public static UpdateOrderRequest updateRequest() {
        return new UpdateOrderRequest(
                PRODUCT_ID, SUPPLIER_ID, RECEIVER_ID,
                DEP_HUB_ID, ARR_HUB_ID,
                20, DEADLINE, "수정된 메모"
        );
    }

    public static CancelOrderRequest cancelRequest() {
        return new CancelOrderRequest("재고 부족");
    }
}
