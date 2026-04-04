package com.shipflow.userservice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.userservice.application.dto.CreateSignupRequestResult;
import com.shipflow.userservice.application.dto.PatchSignupRequestResult;
import com.shipflow.userservice.application.service.SignupRequestService;
import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.error.UserErrorCode;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;
import com.shipflow.userservice.domain.repository.UserRepository;
import com.shipflow.userservice.infrastructure.client.KeycloakUserService;

@ExtendWith(MockitoExtension.class)
public class SignupRequestServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private KeycloakUserService keycloakUserClient;

	@InjectMocks
	private SignupRequestService signupRequestService;

	@DisplayName("회원가입 요청 생성 성공")
	@Test
	void createSignupRequest_success() {
		// given
		UUID keycloakUserId = UUID.randomUUID();

		when(userRepository.existsByUsername("tester")).thenReturn(false);
		when(userRepository.existsBySlackId("slack-1")).thenReturn(false);
		when(keycloakUserClient.createPendingUser("tester", "1234")).thenReturn(keycloakUserId);
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

		// when
		CreateSignupRequestResult result =
			signupRequestService.createSignupRequest("tester", "1234", "테스터", "slack-1");

		// then
		verify(userRepository).save(captor.capture());

		User savedUser = captor.getValue();
		assertThat(savedUser.getId()).isEqualTo(keycloakUserId);
		assertThat(savedUser.getUsername()).isEqualTo("tester");
		assertThat(savedUser.getName()).isEqualTo("테스터");
		assertThat(savedUser.getSlackId()).isEqualTo("slack-1");
		assertThat(savedUser.getStatus()).isEqualTo(UserStatus.PENDING);

		assertThat(result.getId()).isEqualTo(keycloakUserId);
		assertThat(result.getUserName()).isEqualTo("tester");
		assertThat(result.getName()).isEqualTo("테스터");
		assertThat(result.getSlackId()).isEqualTo("slack-1");
		assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
	}

	@DisplayName("회원가입 요청 생성 실패 - username 중복이면 DUPLICATE_USERNAME 예외가 발생한다")
	@Test
	void createSignupRequest_fail_duplicate_username() {
		// given
		when(userRepository.existsByUsername("tester")).thenReturn(true);

		// when / then
		assertThatThrownBy(() ->
			signupRequestService.createSignupRequest("tester", "1234", "테스터", "slack-1"))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_USERNAME);
			});

		verify(keycloakUserClient, never()).createPendingUser(any(), any());
		verify(userRepository, never()).save(any());
	}

	@DisplayName("회원가입 요청 생성 실패 - slackId 중복이면 DUPLICATE_SLACK_ID 예외가 발생한다")
	@Test
	void createSignupRequest_fail_duplicate_slack_id() {
		// given
		when(userRepository.existsByUsername("tester")).thenReturn(false);
		when(userRepository.existsBySlackId("slack-1")).thenReturn(true);

		// when / then
		assertThatThrownBy(() ->
			signupRequestService.createSignupRequest("tester", "1234", "테스터", "slack-1"))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_SLACK_ID);
			});

		verify(keycloakUserClient, never()).createPendingUser(any(), any());
		verify(userRepository, never()).save(any());
	}

	@DisplayName("회원가입 요청 목록 조회 성공")
	@Test
	void getSignupRequestList_success() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		User user1 = new User(UUID.randomUUID(), "tester1", "테스터1", "slack-1");
		ReflectionTestUtils.setField(user1, "createdAt", LocalDateTime.now());
		User user2 = new User(UUID.randomUUID(), "tester2", "테스터2", "slack-2");
		ReflectionTestUtils.setField(user2, "createdAt", LocalDateTime.now());

		when(userRepository.findAllByStatus(UserStatus.PENDING, pageable))
			.thenReturn(new PageImpl<>(List.of(user1, user2), pageable, 2));

		// when
		var result = signupRequestService.getSignupRequestList(UserRole.MASTER, pageable);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalCount()).isEqualTo(2);
		assertThat(result.getPage()).isEqualTo(0);
		assertThat(result.getSize()).isEqualTo(10);
	}

	@DisplayName("회원가입 요청 목록 조회 실패 - 권한이 없으면 ACCESS_DENIED 예외가 발생한다")
	@Test
	void getSignupRequestList_fail_access_denied() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when / then
		assertThatThrownBy(() -> signupRequestService.getSignupRequestList(UserRole.COMPANY_MANAGER, pageable))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.ACCESS_DENIED);
			});

		verify(userRepository, never()).findAllByStatus(any(), any());
	}

	@DisplayName("회원가입 요청 승인 성공")
	@Test
	void patchSignupRequest_success_approve() {
		// given
		UUID userId = UUID.randomUUID();
		User user = new User(userId, "tester", "테스터", "slack-1");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		PatchSignupRequestResult result =
			signupRequestService.patchSignupRequest(
				UserRole.MASTER,
				userId,
				UserStatus.APPROVED,
				UserRole.HUB_MANAGER
			);

		// then
		verify(keycloakUserClient).setUserRole(userId, UserRole.HUB_MANAGER);
		verify(keycloakUserClient).enableUser(userId);

		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getStatus()).isEqualTo(UserStatus.APPROVED);
		assertThat(result.getRole()).isEqualTo(UserRole.HUB_MANAGER);
	}

	@DisplayName("회원가입 요청 거절 성공")
	@Test
	void patchSignupRequest_success_reject() {
		// given
		UUID userId = UUID.randomUUID();
		User user = new User(userId, "tester", "테스터", "slack-1");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		PatchSignupRequestResult result =
			signupRequestService.patchSignupRequest(
				UserRole.MASTER,
				userId,
				UserStatus.REJECTED,
				null
			);

		// then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getStatus()).isEqualTo(UserStatus.REJECTED);
	}

	@DisplayName("회원가입 요청 처리 실패 - 권한이 없으면 ACCESS_DENIED 예외가 발생한다")
	@Test
	void patchSignupRequest_fail_access_denied() {
		// given
		UUID userId = UUID.randomUUID();

		// when / then
		assertThatThrownBy(() ->
			signupRequestService.patchSignupRequest(
				UserRole.COMPANY_MANAGER,
				userId,
				UserStatus.APPROVED,
				UserRole.HUB_MANAGER
			))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.ACCESS_DENIED);
			});

		verify(userRepository, never()).findById(any());
	}

	@DisplayName("회원가입 요청 처리 실패 - 대상 유저가 없으면 USER_NOT_FOUND 예외가 발생한다")
	@Test
	void patchSignupRequest_fail_user_not_found() {
		// given
		UUID userId = UUID.randomUUID();

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() ->
			signupRequestService.patchSignupRequest(
				UserRole.MASTER,
				userId,
				UserStatus.APPROVED,
				UserRole.HUB_MANAGER
			))
			.isInstanceOf(BusinessException.class)
			.satisfies(ex -> {
				BusinessException be = (BusinessException) ex;
				assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}
}

