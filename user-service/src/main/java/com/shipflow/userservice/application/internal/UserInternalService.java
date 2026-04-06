package com.shipflow.userservice.application.internal;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.userservice.application.internal.dto.GetInternalUserResult;
import com.shipflow.userservice.application.internal.dto.PatchInternalUserCommand;
import com.shipflow.userservice.application.internal.dto.PatchInternalUserResult;
import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.error.UserErrorCode;
import com.shipflow.userservice.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInternalService {

	private final UserRepository userRepository;

	public GetInternalUserResult getUser(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		if (user.isDeleted()){
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}

		return new GetInternalUserResult(user.getId(), user.getName(), user.getSlackId(), user.getHubId(), user.getCompanyId());
	}

	@Transactional
	public PatchInternalUserResult updateUser(UUID userId, PatchInternalUserCommand command) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		if (command.isHubIdUpdated()) {
			user.changeHubId(command.getHubId());
		}
		if (command.isCompanyIdUpdated()) {
			user.changeCompanyId(command.getCompanyId());
		}

		return new PatchInternalUserResult(user.getId(), user.getHubId(), user.getCompanyId());
	}
}
