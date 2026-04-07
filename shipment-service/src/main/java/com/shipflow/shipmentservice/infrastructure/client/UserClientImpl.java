package com.shipflow.shipmentservice.infrastructure.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.application.client.UserClient;
import com.shipflow.shipmentservice.application.client.dto.UserInfo;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

	private final UserFeignClient userFeignClient;

	@Override
	public UserInfo getUser(UUID userId) {
		try {
			return userFeignClient.getUser(userId).getData();
		} catch (FeignException.NotFound e) {
			throw new BusinessException(ShipmentErrorCode.USER_NOT_FOUND);
		} catch (FeignException e) {
			throw new BusinessException(ShipmentErrorCode.USER_SERVICE_UNAVAILABLE);
		}
	}
}
