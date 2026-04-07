package com.shipflow.orderservice.infrastructure.client.adapter;

import com.shipflow.orderservice.domain.exception.ExternalServiceException;
import com.shipflow.orderservice.domain.exception.UserNotFoundException;
import com.shipflow.orderservice.infrastructure.client.UserFeignClient;
import com.shipflow.orderservice.infrastructure.client.dto.UserInfo;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserClientAdapter {

    private final UserFeignClient userFeignClient;

    @Retryable(
            retryFor = {RetryableException.class},
            noRetryFor = {UserNotFoundException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2)
    )
    public UserInfo fetch(UUID userId) {
        try {
            return userFeignClient.getUserInfo("true", userId).getData();
        }catch (RetryableException e) {
            throw e;
        }
        catch (feign.FeignException.NotFound e) {
            throw new UserNotFoundException();
        } catch (feign.FeignException e) {
            throw new ExternalServiceException(e);
        }
    }
}
