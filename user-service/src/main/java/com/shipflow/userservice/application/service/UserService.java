package com.shipflow.userservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.userservice.application.dto.GetUsersListResult;
import com.shipflow.userservice.application.dto.GetUsersResult;
import com.shipflow.userservice.application.dto.PatchMyInfoCommand;
import com.shipflow.userservice.application.dto.PatchUserCommand;
import com.shipflow.userservice.application.dto.PatchUserResult;
import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.error.UserErrorCode;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;
import com.shipflow.userservice.domain.repository.UserRepository;
import com.shipflow.userservice.infrastructure.client.ClientApiResponse;
import com.shipflow.userservice.infrastructure.client.CompanyFeignClient;
import com.shipflow.userservice.infrastructure.client.KeycloakUserService;
import com.shipflow.userservice.infrastructure.client.ShipmentFeignClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final KeycloakUserService keycloakUserClient;
	private final CompanyFeignClient companyFeignClient;
	private final ShipmentFeignClient shipmentFeignClient;

	public GetUsersResult getUsers(UserRole requestRole, UUID userId) { //userId 단건조회
		validateUserServiceAccess(requestRole);
		User user = getUser(userId);

		return new GetUsersResult(user);
	}

	public GetUsersListResult getUsersList(UserRole requestRole, Pageable pageable, UserRole role, UserStatus status, String keyword) { //사용자 리스트 조회
		validateUserServiceAccess(requestRole);

		Page<User> userPage = userRepository.searchUsers(role, status, keyword, pageable);

		List<GetUsersResult> content = userPage.getContent().stream()
			.map(user -> new GetUsersResult(user)).toList();

		return new GetUsersListResult(
			content,
			userPage.getNumber(),
			userPage.getSize(),
			userPage.getTotalElements()
		);
	}

	@Transactional
	public PatchUserResult updateUser(UserRole requestRole, UUID userId, PatchUserCommand command) {
		validateUserServiceAccess(requestRole);
		User user = getUser(userId);

		if(command.getName() != null) {
			user.changeName(command.getName());
		}
		if(command.getSlackId() != null) {
			user.changeSlackId(command.getSlackId());
		}
		if(command.getRole() != null) {
			UserRole currentRole = user.getRole();
			keycloakUserClient.updateUserRole(user.getId(), currentRole, command.getRole());
			user.changeRole(command.getRole());
		}

		return new PatchUserResult(user.getId(), user.getName(), user.getSlackId(), user.getRole() );
	}

	@Transactional
	public void deleteUser(UserRole requestRole, UUID userId) {
		validateUserServiceAccess(requestRole);
		User user = getUser(userId);

		UserRole userRole = user.getRole();
		if(userRole.isCompanyManager()){
			if(user.getCompanyId() != null) {
				try {
					ClientApiResponse<Void> response = companyFeignClient.deleteManager(user.getId());
					if (response == null || !response.isSuccess()) {
						throw new BusinessException(UserErrorCode.DELETE_REQUEST_FAILED);
					}
				} catch (FeignException e) {
					throw new BusinessException(UserErrorCode.DELETE_REQUEST_FAILED);
				}
			}
		} else if (userRole.isHubManager()) {
			if(user.getHubId() != null){
				throw new BusinessException(UserErrorCode.HUB_MANAGER_DELETE_FORBIDDEN);
			}
		} else if(userRole.isShipmentManager()) {
			try {
				ClientApiResponse<Void> response = shipmentFeignClient.patchManager(user.getId());
				if (response == null || !response.isSuccess()) {
					throw new BusinessException(UserErrorCode.DELETE_REQUEST_FAILED);
				}
			} catch (FeignException e) {
				throw new BusinessException(UserErrorCode.DELETE_REQUEST_FAILED);
			}
		}

		keycloakUserClient.disableUser(user.getId());
		user.softDeleted(userId);
	}

	@Transactional
	public PatchUserResult updateMe(UUID userId, PatchMyInfoCommand command) {
		User user = getUser(userId);
		user.updateMyInfo(command.getName(), command.getSlack());
		return new PatchUserResult(userId, user.getName(), user.getSlackId(), user.getRole());
	}

	public GetUsersResult getMyInfo(UUID userId) {
		User user = getUser(userId);
		return new GetUsersResult(user);
	}

	private void validateUserServiceAccess(UserRole requestRole) {
		if (requestRole == null || !requestRole.canUseUserService()) {
			throw new BusinessException(UserErrorCode.ACCESS_DENIED);
		}
	}

	private User getUser(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		if (user.isDeleted()) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}
		return user;
	}
}
