package com.shipflow.shipmentservice.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.application.client.HubClient;
import com.shipflow.shipmentservice.application.client.dto.HubRouteResult;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubClientImpl implements HubClient {

	private final HubFeignClient hubFeignClient;

	@Override
	public List<HubRouteResult> getHubRoutes(UUID departureHubId, UUID arrivalHubId) {
		try {
			ApiResponse<List<HubRouteResult>> response = hubFeignClient.getHubRoutes(departureHubId, arrivalHubId);

			List<HubRouteResult> routes = response.getData();
			if (routes == null || routes.isEmpty()) {
				throw new BusinessException(ShipmentErrorCode.HUB_ROUTE_NOT_FOUND);
			}

			return routes;
		} catch (BusinessException e) {
			throw e;
		} catch (FeignException.NotFound e) {
			log.warn("[HubClient] 허브 경로 없음 | departureHubId={} | arrivalHubId={}", departureHubId, arrivalHubId);
			throw new BusinessException(ShipmentErrorCode.HUB_ROUTE_NOT_FOUND);
		} catch (FeignException e) {
			log.error("[HubClient] 허브 서비스 호출 실패 | status={} | message={}", e.status(), e.getMessage());
			throw new BusinessException(ShipmentErrorCode.HUB_SERVICE_UNAVAILABLE);
		}
	}
}
