package com.shipflow.userservice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.userservice.application.dto.GetUsersResult;
import com.shipflow.userservice.application.dto.PatchMyInfoCommand;
import com.shipflow.userservice.application.dto.PatchUserCommand;
import com.shipflow.userservice.application.dto.PatchUserResult;
import com.shipflow.userservice.application.service.UserService;
import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.error.UserErrorCode;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;
import com.shipflow.userservice.domain.repository.UserRepository;
import com.shipflow.userservice.infrastructure.client.KeycloakUserService;

@ExtendWith(MockitoExtension.class)
@EnableJpaAuditing
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private KeycloakUserService keycloakUserClient;

	@InjectMocks
	private UserService userService;

	@DisplayName("내 정보 조회 성공")
	@Test
	void getMyInfo_success() {
		// given
		UUID userId = UUID.randomUUID();
		User user = new User(userId, "tester", "테스터", "slack-1");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		GetUsersResult result = userService.getMyInfo(userId);

		// then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getUserName()).isEqualTo("tester");
		assertThat(result.getName()).isEqualTo("테스터");
		assertThat(result.getSlackId()).isEqualTo("slack-1");
	}

	@DisplayName("내 정보 조회 실패 - 유저가 없으면 USER_NOT_FOUND 예외가 발생한다")
	@Test
	void getMyInfo_fail_user_not_found() {
		// given
		UUID userId = UUID.randomUUID();

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> userService.getMyInfo(userId))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	@DisplayName("내 정보 수정 성공")
	@Test
	void updateMe_success() {
		// given
		UUID userId = UUID.randomUUID();
		User user = new User(userId, "tester", "기존이름", "old-slack");

		PatchMyInfoCommand command = new PatchMyInfoCommand("새이름", "new-slack");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		PatchUserResult result = userService.updateMe(userId, command);

		// then
		assertThat(result.getId()).isEqualTo(userId);
		assertThat(result.getName()).isEqualTo("새이름");
		assertThat(result.getSlackId()).isEqualTo("new-slack");
	}

	@DisplayName("내 정보 수정 실패 - 유저가 없으면 USER_NOT_FOUND 예외가 발생한다")
	@Test
	void updateMe_fail_user_not_found() {
		// given
		UUID userId = UUID.randomUUID();
		PatchMyInfoCommand command = new PatchMyInfoCommand("새이름", "new-slack");

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> userService.updateMe(userId, command))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	@DisplayName("유저 단건 조회 성공 - MASTER는 특정 유저를 조회할 수 있다")
	@Test
	void getUsers_success() {
		// given
		UUID userId = UUID.randomUUID();
		User user = new User(userId, "tester", "테스터", "slack-1");
		user.approve(UserRole.HUB_MANAGER);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		GetUsersResult result = userService.getUsers(UserRole.MASTER, userId);

		// then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getUserName()).isEqualTo("tester");
		assertThat(result.getName()).isEqualTo("테스터");
		assertThat(result.getSlackId()).isEqualTo("slack-1");
		assertThat(result.getRole()).isEqualTo(UserRole.HUB_MANAGER);
		assertThat(result.getStatus()).isEqualTo(UserStatus.APPROVED);
	}

	@DisplayName("유저 단건 조회 실패 - MASTER가 아니면 ACCESS_DENIED 예외가 발생한다")
	@Test
	void getUsers_fail_access_denied() {
		// given
		UUID userId = UUID.randomUUID();

		// when / then
		assertThatThrownBy(() -> userService.getUsers(UserRole.HUB_MANAGER, userId))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.ACCESS_DENIED);
			});

		verify(userRepository, never()).findById(any());
	}

	@DisplayName("유저 수정 성공 - 이름, 슬랙아이디, 역할을 수정할 수 있다")
	@Test
	void updateUser_success() {
		// given
		UUID userId = UUID.randomUUID();
		User user = new User(userId, "tester", "기존이름", "old-slack");
		user.approve(UserRole.HUB_MANAGER);

		PatchUserCommand command =
			new PatchUserCommand("새이름", "new-slack", UserRole.COMPANY_MANAGER);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		PatchUserResult result = userService.updateUser(UserRole.MASTER, userId, command);

		// then
		verify(keycloakUserClient).updateUserRole(userId, UserRole.HUB_MANAGER, UserRole.COMPANY_MANAGER);

		assertThat(result.getId()).isEqualTo(userId);
		assertThat(result.getName()).isEqualTo("새이름");
		assertThat(result.getSlackId()).isEqualTo("new-slack");
		assertThat(result.getRole()).isEqualTo(UserRole.COMPANY_MANAGER);
	}

	@DisplayName("유저 수정 실패 - MASTER가 아니면 ACCESS_DENIED 예외가 발생한다")
	@Test
	void updateUser_fail_access_denied() {
		// given
		UUID userId = UUID.randomUUID();
		PatchUserCommand command =
			new PatchUserCommand("새이름", "new-slack", UserRole.COMPANY_MANAGER);

		// when / then
		assertThatThrownBy(() -> userService.updateUser(UserRole.HUB_MANAGER, userId, command))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.ACCESS_DENIED);
			});

		verify(userRepository, never()).findById(any());
	}

	@DisplayName("유저 수정 실패 - 대상 유저가 없으면 USER_NOT_FOUND 예외가 발생한다")
	@Test
	void updateUser_fail_user_not_found() {
		// given
		UUID userId = UUID.randomUUID();
		PatchUserCommand command =
			new PatchUserCommand("새이름", "new-slack", UserRole.COMPANY_MANAGER);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> userService.updateUser(UserRole.MASTER, userId, command))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	@DisplayName("유저 삭제 성공")
	@Test
	void deleteUser_success() {
		// given
		UUID userId = UUID.randomUUID();
		User user = mock(User.class);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(user.getId()).thenReturn(userId);

		// when
		userService.deleteUser(UserRole.MASTER, userId);

		// then
		verify(keycloakUserClient).disableUser(userId);
		verify(user).softDeleted(userId);
	}

	@DisplayName("유저 삭제 실패 - MASTER가 아니면 ACCESS_DENIED 예외가 발생한다")
	@Test
	void deleteUser_fail_access_denied() {
		// given
		UUID userId = UUID.randomUUID();

		// when / then
		assertThatThrownBy(() -> userService.deleteUser(UserRole.HUB_MANAGER, userId))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.ACCESS_DENIED);
			});

		verify(userRepository, never()).findById(any());
	}
}
