package com.shipflow.orderservice.infrastructure.client.adapter;

import com.shipflow.orderservice.domain.exception.ExternalServiceException;
import com.shipflow.orderservice.domain.exception.InsufficientStockException;
import com.shipflow.orderservice.domain.exception.ProductNotFoundException;
import com.shipflow.orderservice.infrastructure.client.ProductFeignClient;
import com.shipflow.orderservice.infrastructure.client.dto.ProductInfo;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductClientAdapter {

    private final ProductFeignClient productFeignClient;

    @Retryable(
            retryFor = {RetryableException.class},
            noRetryFor = {ProductNotFoundException.class, InsufficientStockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2)
    )
    public ProductInfo fetch(String ordererId, UUID productId, int quantity) {
        try {
            return productFeignClient.getProductInfo("true", ordererId, productId, quantity).getData();
        } catch (RetryableException e) {
            throw e;
        } catch (feign.FeignException.NotFound e) {
            throw new ProductNotFoundException();
        } catch (feign.FeignException.Conflict e) {
            throw new InsufficientStockException();
        } catch (feign.FeignException e) {
            throw new ExternalServiceException(e);
        }
    }
}
