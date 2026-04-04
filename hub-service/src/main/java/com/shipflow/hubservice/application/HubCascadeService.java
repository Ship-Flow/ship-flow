package com.shipflow.hubservice.application;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shipflow.hubservice.infrastructure.client.CompanyClient;
import com.shipflow.hubservice.infrastructure.client.DeliveryClient;
import com.shipflow.hubservice.infrastructure.client.UserClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubCascadeService {

	private final UserClient userClient;
	private final DeliveryClient deliveryClient;
	private final CompanyClient companyClient;

	public void cascadeDeleteHub(UUID hubId, UUID managerId, UUID requestUserId) {
		String requestUserIdStr = requestUserId.toString();
		deliveryClient.deleteCompanyDeliveryManagers("true", requestUserIdStr, hubId);
		companyClient.deleteCompaniesByHub("true", requestUserIdStr, hubId);
		userClient.updateUserHubAssignment(
			"true",
			requestUserIdStr,
			managerId,
			new UserClient.UpdateUserHubAssignmentRequest(null, LocalDateTime.now().toString())
		);
	}

	public void cascadeUpdateManagerAssignment(
		UUID oldManagerId, UUID newManagerId, UUID hubId, UUID requestUserId) {
		String requestUserIdStr = requestUserId.toString();
		// 1. 기존 담당자: hubId를 null로 해제
		userClient.updateUserHubAssignment(
			"true",
			requestUserIdStr,
			oldManagerId,
			new UserClient.UpdateUserHubAssignmentRequest(null, LocalDateTime.now().toString())
		);
		// 2. 신규 담당자: hubId를 해당 허브로 지정
		userClient.updateUserHubAssignment(
			"true",
			requestUserIdStr,
			newManagerId,
			new UserClient.UpdateUserHubAssignmentRequest(hubId, LocalDateTime.now().toString())
		);
	}
}
