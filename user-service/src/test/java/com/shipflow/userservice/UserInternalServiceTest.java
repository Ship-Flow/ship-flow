package com.shipflow.userservice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.userservice.application.internal.UserInternalService;
import com.shipflow.userservice.application.internal.dto.GetInternalUserResult;
import com.shipflow.userservice.application.internal.dto.PatchInternalUserCommand;
import com.shipflow.userservice.application.internal.dto.PatchInternalUserResult;
import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.error.UserErrorCode;
import com.shipflow.userservice.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserInternalServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserInternalService userInternalService;

	@DisplayName("내부 유저 조회 성공 - id, name, slackId를 조회할 수 있다")
	@Test
	void getUser_success() {
		// given
		UUID userId = UUID.randomUUID();
		User user = new User(userId, "tester", "테스터", "slack-1");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		GetInternalUserResult result = userInternalService.getUser(userId);

		// then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getName()).isEqualTo("테스터");
		assertThat(result.getSlackId()).isEqualTo("slack-1");
	}

	@DisplayName("내부 유저 조회 실패 - 유저가 없으면 USER_NOT_FOUND 예외가 발생한다")
	@Test
	void getUser_fail_user_not_found() {
		// given
		UUID userId = UUID.randomUUID();

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> userInternalService.getUser(userId))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	@DisplayName("내부 유저 수정 성공 - hubId를 수정할 수 있다")
	@Test
	void updateUser_success_change_hubId() {
		// given
		UUID userId = UUID.randomUUID();
		UUID hubId = UUID.randomUUID();
		boolean hubIdUpdated = true;
		boolean companyIdUpdated = false;
		User user = new User(userId, "tester", "테스터", "slack-1");

		PatchInternalUserCommand command =
			new PatchInternalUserCommand(hubId, null, hubIdUpdated, companyIdUpdated);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		PatchInternalUserResult result = userInternalService.updateUser(userId, command);

		// then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getHubId()).isEqualTo(hubId);
		assertThat(result.getCompanyId()).isNull();
	}

	@DisplayName("내부 유저 수정 성공 - companyId를 수정할 수 있다")
	@Test
	void updateUser_success_change_companyId() {
		// given
		UUID userId = UUID.randomUUID();
		UUID companyId = UUID.randomUUID();
		boolean hubIdUpdated = false;
		boolean companyIdUpdated = true;
		User user = new User(userId, "tester", "테스터", "slack-1");

		PatchInternalUserCommand command =
			new PatchInternalUserCommand(null, companyId, hubIdUpdated, companyIdUpdated);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		PatchInternalUserResult result = userInternalService.updateUser(userId, command);

		// then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getHubId()).isNull();
		assertThat(result.getCompanyId()).isEqualTo(companyId);
	}

	@DisplayName("내부 유저 수정 성공 - hubId와 companyId를 모두 수정할 수 있다")
	@Test
	void updateUser_success_change_hubId_and_companyId() {
		// given
		UUID userId = UUID.randomUUID();
		UUID hubId = UUID.randomUUID();
		UUID companyId = UUID.randomUUID();
		boolean hubIdUpdated = true;
		boolean companyIdUpdated = true;
		User user = new User(userId, "tester", "테스터", "slack-1");

		PatchInternalUserCommand command =
			new PatchInternalUserCommand(hubId, companyId, hubIdUpdated, companyIdUpdated);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		PatchInternalUserResult result = userInternalService.updateUser(userId, command);

		// then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getHubId()).isEqualTo(hubId);
		assertThat(result.getCompanyId()).isEqualTo(companyId);
	}

	@DisplayName("내부 유저 수정 성공 - hubId를 null로 변경할 수 있다")
	@Test
	void updateUser_success_clear_hubId() {
		// given
		UUID userId = UUID.randomUUID();
		UUID originalHubId = UUID.randomUUID();
		boolean hubIdUpdated = true;
		boolean companyIdUpdated = false;

		User user = new User(userId, "tester", "테스터", "slack-1");
		user.changeHubId(originalHubId);

		PatchInternalUserCommand command =
			new PatchInternalUserCommand(null, null, hubIdUpdated, companyIdUpdated);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		PatchInternalUserResult result = userInternalService.updateUser(userId, command);

		// then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getHubId()).isNull();
	}

	@DisplayName("내부 유저 수정 실패 - 유저가 없으면 USER_NOT_FOUND 예외가 발생한다")
	@Test
	void updateUser_fail_user_not_found() {
		// given
		UUID userId = UUID.randomUUID();
		boolean hubIdUpdated = false;
		boolean companyIdUpdated = false;

		PatchInternalUserCommand command =
			new PatchInternalUserCommand(UUID.randomUUID(), null, hubIdUpdated, companyIdUpdated);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> userInternalService.updateUser(userId, command))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}
}
