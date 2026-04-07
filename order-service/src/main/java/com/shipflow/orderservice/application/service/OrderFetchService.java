package com.shipflow.orderservice.application.service;

import com.shipflow.orderservice.application.dto.CreateOrderCommand;
import com.shipflow.orderservice.infrastructure.client.adapter.CompanyClientAdapter;
import com.shipflow.orderservice.infrastructure.client.adapter.ProductClientAdapter;
import com.shipflow.orderservice.infrastructure.client.adapter.UserClientAdapter;
import com.shipflow.orderservice.infrastructure.client.dto.ProductInfo;
import com.shipflow.orderservice.infrastructure.client.dto.ReceiverCompanyInfo;
import com.shipflow.orderservice.infrastructure.client.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class OrderFetchService {

    private final ProductClientAdapter productAdapter;
    private final UserClientAdapter userAdapter;
    private final CompanyClientAdapter companyAdapter;

    public CreateOrderCommand fetchAndBuild(UUID ordererId, UUID productId,
                                            int quantity, LocalDateTime deadline, String note) {
        // Step 1: product, user 병렬 호출
        CompletableFuture<ProductInfo> productFuture = CompletableFuture.supplyAsync(
                () -> productAdapter.fetch(ordererId.toString(), productId, quantity));
        CompletableFuture<UserInfo> userFuture = CompletableFuture.supplyAsync(
                () -> userAdapter.fetch(ordererId));

        try {
            CompletableFuture.allOf(productFuture, userFuture).join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            if (cause instanceof Error err) {
                throw err;
            }
            throw e;
        }

        ProductInfo product = productFuture.join();
        UserInfo user = userFuture.join();

        // Step 2: receiverCompanyId 확보 후 company 호출
        ReceiverCompanyInfo company = companyAdapter.fetch(user.receiverCompanyId());

        return new CreateOrderCommand(
                ordererId,
                user.ordererName(),
                productId,
                product.productName(),
                product.supplierCompanyId(),
                product.supplierCompanyName(),
                user.receiverCompanyId(),
                company.companyName(),
                product.departureHubId(),
                company.hubId(),
                quantity,
                deadline,
                note,
                company.address()
        );
    }
}
