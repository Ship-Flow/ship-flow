package com.shipflow.userservice.application.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.userservice.application.dto.CreateSignupRequestResult;
import com.shipflow.userservice.application.dto.GetSignupRequestResult;
import com.shipflow.userservice.application.dto.PatchSignupRequestResult;
import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.error.UserErrorCode;
import com.shipflow.userservice.domain.repository.UserRepository;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;
import com.shipflow.userservice.infrastructure.client.KeycloakUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignupRequestService {

	private final UserRepository userRepository;
	private final KeycloakUserService keycloakUserClient;

	@Transactional
	public CreateSignupRequestResult createSignupRequest(String username, String password, String name, String slackId) {
		if (userRepository.existsByUsername(username)) {
			throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
		}
		if (userRepository.existsBySlackId(slackId)) {
			throw new BusinessException(UserErrorCode.DUPLICATE_SLACK_ID);
		}

		UUID keycloakUserId = keycloakUserClient.createPendingUser(username, password);

		try {
			User user = new User(keycloakUserId, username, name, slackId);
			User savedUser = userRepository.save(user);

			return new CreateSignupRequestResult(
				savedUser.getId(),
				savedUser.getUsername(),
				savedUser.getName(),
				savedUser.getSlackId(),
				savedUser.getStatus(),
				savedUser.getCreatedAt() != null ? savedUser.getCreatedAt().toString() : LocalDateTime.now().toString()
			);
		} catch (Exception e) {
			keycloakUserClient.deleteUser(keycloakUserId);
			throw e;
		}
	}

	public GetSignupRequestResult getSignupRequestList(UserRole requestRole, Pageable pageable) {
		validateUserServiceAccess(requestRole);
		Page<User> users = userRepository.findAllByStatus(UserStatus.PENDING, pageable);

		List<GetSignupRequestResult.SignupRequest> requests = users.getContent().stream()
			.map(request -> new GetSignupRequestResult.SignupRequest(
				request.getId(),
				request.getUsername(),
				request.getName(),
				request.getSlackId(),
				request.getStatus(),
				request.getCreatedAt().toString()
			))
			.collect(Collectors.toList());

		return new GetSignupRequestResult(
			requests,
			users.getNumber(),
			users.getSize(),
			users.getTotalElements()
		);
	}

	@Transactional
	public PatchSignupRequestResult patchSignupRequest(UserRole reauetRole, UUID userId, UserStatus status, UserRole role) {
		validateUserServiceAccess(reauetRole);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		if (status == UserStatus.APPROVED)
			if (role != null) {
				user.approve(role);
				keycloakUserClient.setUserRole(userId, role);
				keycloakUserClient.enableUser(userId);
				return new PatchSignupRequestResult(userId, status, role);
			}

		if (status == UserStatus.REJECTED) {
			user.reject();
			return new PatchSignupRequestResult(userId, status, role);
		}

		throw new BusinessException(UserErrorCode.INVALID_REQUEST);
	}

	private void validateUserServiceAccess(UserRole requestRole) {
		if (!requestRole.canReplySignupRequest()) {
			throw new BusinessException(UserErrorCode.ACCESS_DENIED);
		}
	}
}
