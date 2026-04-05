package com.shipflow.hubservice.application;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.shipflow.hubservice.infrastructure.client.CompanyClient;
import com.shipflow.hubservice.infrastructure.client.DeliveryClient;
import com.shipflow.hubservice.infrastructure.client.UserClient;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubCascadeService {

	private final UserClient userClient;
	private final DeliveryClient deliveryClient;
	private final CompanyClient companyClient;

	@Retryable(
		retryFor = {FeignException.class, RetryableException.class},
		noRetryFor = {FeignException.BadRequest.class, FeignException.NotFound.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2)
	)
	public void deleteDeliveryManagers(UUID hubId, UUID requestUserId) {
		log.info("허브 배송담당자 삭제 요청: hubId={}", hubId);
		deliveryClient.deleteCompanyDeliveryManagers("true", requestUserId.toString(), hubId);
	}

	@Retryable(
		retryFor = {FeignException.class, RetryableException.class},
		noRetryFor = {FeignException.BadRequest.class, FeignException.NotFound.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2)
	)
	public void deleteCompanies(UUID hubId, UUID requestUserId) {
		log.info("허브 업체 삭제 요청: hubId={}", hubId);
		companyClient.deleteCompaniesByHub("true", requestUserId.toString(), hubId);
	}

	@Retryable(
		retryFor = {FeignException.class, RetryableException.class},
		noRetryFor = {FeignException.BadRequest.class, FeignException.NotFound.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2)
	)
	public void unassignManager(UUID managerId, UUID requestUserId) {
		log.info("허브 담당자 해제 요청: managerId={}", managerId);
		userClient.updateUserHubAssignment(
			"true",
			requestUserId.toString(),
			managerId,
			new UserClient.UpdateUserHubAssignmentRequest(null, LocalDateTime.now().toString())
		);
	}

	@Retryable(
		retryFor = {FeignException.class, RetryableException.class},
		noRetryFor = {FeignException.BadRequest.class, FeignException.NotFound.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2)
	)
	public void assignManager(UUID managerId, UUID hubId, UUID requestUserId) {
		log.info("허브 담당자 지정 요청: managerId={}, hubId={}", managerId, hubId);
		userClient.updateUserHubAssignment(
			"true",
			requestUserId.toString(),
			managerId,
			new UserClient.UpdateUserHubAssignmentRequest(hubId, LocalDateTime.now().toString())
		);
	}
}
