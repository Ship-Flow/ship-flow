package com.shipflow.orderservice.infrastructure.client;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.orderservice.infrastructure.client.dto.ProductInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "productservice")
public interface ProductFeignClient {

    @GetMapping("/internal/products/{productId}")
    ApiResponse<ProductInfo> getProductInfo(
            @RequestHeader("X-Internal-Request") String internalRequest,
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("productId") UUID productId,
            @RequestParam("quantity") int quantity
    );
}
