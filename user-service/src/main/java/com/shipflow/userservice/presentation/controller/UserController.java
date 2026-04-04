package com.shipflow.userservice.presentation.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.common.exception.BusinessException;
import com.shipflow.userservice.application.dto.GetUsersListResult;
import com.shipflow.userservice.application.dto.GetUsersResult;
import com.shipflow.userservice.application.dto.PatchMyInfoCommand;
import com.shipflow.userservice.application.dto.PatchUserCommand;
import com.shipflow.userservice.application.dto.PatchUserResult;
import com.shipflow.userservice.application.service.UserService;
import com.shipflow.userservice.domain.error.UserErrorCode;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;
import com.shipflow.userservice.presentation.dto.request.PatchMyInfoReqDto;
import com.shipflow.userservice.presentation.dto.request.PatchUserReqDto;
import com.shipflow.userservice.presentation.dto.response.GetUserResDto;
import com.shipflow.userservice.presentation.dto.response.GetUsersListResDto;
import com.shipflow.userservice.presentation.dto.response.PatchUserResDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<GetUserResDto>> getUser(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID userId) {
		UserRole requestRole = extractRole(jwt);

		GetUsersResult result = userService.getUsers(requestRole, userId);
		GetUserResDto response = new GetUserResDto(result);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<GetUsersListResDto>> getUsersList(
		@AuthenticationPrincipal Jwt jwt,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sortBy,
		@RequestParam(defaultValue = "DESC") String sortDirection,
		@RequestParam(required = false) UserRole role,
		@RequestParam(defaultValue = "APPROVED") UserStatus status
		) {

		if (size != 10 && size != 30 && size != 50) //조회 사이즈 제한
			size = 10;

		UserRole requestRole = extractRole(jwt);
		Sort.Direction direction = Sort.Direction.fromString(sortDirection);
		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

		GetUsersListResult result = userService.getUsersList(requestRole, pageable, role, status);
		GetUsersListResDto response = new GetUsersListResDto(result);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<ApiResponse<PatchUserResDto>> updateUser(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable UUID userId,
		@RequestBody PatchUserReqDto request
	) {
		UserRole requestRole = extractRole(jwt);

		PatchUserCommand command = new PatchUserCommand(request.getName(), request.getSlackId(), request.getRole());
		PatchUserResult result = userService.updateUser(requestRole, userId, command);
		PatchUserResDto response = new PatchUserResDto(result);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse<Void>> deleteUser(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable UUID userId
	) {
		UserRole requestRole = extractRole(jwt);

		userService.deleteUser(requestRole, userId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(null));
	}

	@GetMapping("/me") //본인정보 조회
	public ResponseEntity<ApiResponse<GetUserResDto>> getMyInfo(@AuthenticationPrincipal Jwt jwt) {
		UUID userId = UUID.fromString(jwt.getSubject());

		GetUsersResult result = userService.getMyInfo(userId);
		GetUserResDto response = new GetUserResDto(result);

		return ResponseEntity.ok(ApiResponse.ok(response));
	}

	@PatchMapping("/me") //본인정보 수정
	public ResponseEntity<ApiResponse<PatchUserResDto>> updateMe(
		@AuthenticationPrincipal Jwt jwt,
		@RequestBody PatchMyInfoReqDto request
	) {
		UUID userId = UUID.fromString(jwt.getSubject());

		PatchMyInfoCommand command = new PatchMyInfoCommand(
			request.getName(),
			request.getSlackId()
		);

		PatchUserResult result = userService.updateMe(userId, command);
		PatchUserResDto response = new PatchUserResDto(result);

		return ResponseEntity.ok(ApiResponse.ok(response));
	}

	public static UserRole extractRole(Jwt jwt) {
		Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
		Object rolesObj = realmAccess.get("roles");

		if (rolesObj instanceof List<?> roles && !roles.isEmpty()) {
			return UserRole.valueOf(roles.get(0).toString());
		}

		throw new BusinessException(UserErrorCode.ACCESS_DENIED);
	}
}
