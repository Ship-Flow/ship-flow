package com.shipflow.orderservice.infrastructure.client.adapter;

import com.shipflow.orderservice.domain.exception.CompanyNotFoundException;
import com.shipflow.orderservice.domain.exception.ExternalServiceException;
import com.shipflow.orderservice.infrastructure.client.CompanyFeignClient;
import com.shipflow.orderservice.infrastructure.client.dto.ReceiverCompanyInfo;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyClientAdapter {

    private final CompanyFeignClient companyFeignClient;

    @Retryable(
            retryFor = {RetryableException.class},
            noRetryFor = {CompanyNotFoundException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2)
    )
    public ReceiverCompanyInfo fetch(UUID companyId) {
        try {
            return companyFeignClient.getCompanyInfo("true", companyId);
        } catch (RetryableException e) {
            throw e;
        } catch (feign.FeignException.NotFound e) {
            throw new CompanyNotFoundException();
        } catch (feign.FeignException e) {
            throw new ExternalServiceException(e);
        }
    }
}
