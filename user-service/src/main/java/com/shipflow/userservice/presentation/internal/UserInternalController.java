package com.shipflow.userservice.presentation.internal;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.userservice.application.internal.UserInternalService;
import com.shipflow.userservice.application.internal.dto.GetInternalUserResult;
import com.shipflow.userservice.application.internal.dto.PatchInternalUserCommand;
import com.shipflow.userservice.application.internal.dto.PatchInternalUserResult;
import com.shipflow.userservice.presentation.internal.dto.GetInternalUserResDto;
import com.shipflow.userservice.presentation.internal.dto.PatchInternalUserReqDto;
import com.shipflow.userservice.presentation.internal.dto.PatchInternalUserResDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

	private final UserInternalService userInternalService;

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<GetInternalUserResDto>> getUser(@PathVariable UUID userId) {
		GetInternalUserResult result = userInternalService.getUser(userId);
		GetInternalUserResDto response = new GetInternalUserResDto(result);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<ApiResponse<PatchInternalUserResDto>> updateUser(
		@PathVariable UUID userId,
		@RequestBody PatchInternalUserReqDto request
	) {
		PatchInternalUserCommand command = new PatchInternalUserCommand(
			request.getHubId(),
			request.getCompanyId(),
			request.isHubIdUpdated(),
			request.isCompanyIdUpdated()
		);

		PatchInternalUserResult result = userInternalService.updateUser(userId, command);
		PatchInternalUserResDto response = new PatchInternalUserResDto(result);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}
}
