package com.shipflow.userservice.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.userservice.application.dto.CreateSignupRequestResult;
import com.shipflow.userservice.application.dto.GetSignupRequestResult;
import com.shipflow.userservice.application.dto.PatchSignupRequestResult;
import com.shipflow.userservice.application.service.SignupRequestService;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.presentation.dto.request.PatchSignupRequestReqDto;
import com.shipflow.userservice.presentation.dto.request.PostSignupRequestReqDto;
import com.shipflow.userservice.presentation.dto.response.GetSignupRequestListResDto;
import com.shipflow.userservice.presentation.dto.response.PatchSignupRequestResDto;
import com.shipflow.userservice.presentation.dto.response.PostSignupRequestResDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/signup-requests")
@RequiredArgsConstructor
public class SignupRequestController {

	private final SignupRequestService signupService;

	//회원가입 요청
	@PostMapping
	public ResponseEntity<ApiResponse<PostSignupRequestResDto>> createSignupRequest(
		@RequestBody PostSignupRequestReqDto request
	){
		CreateSignupRequestResult result = signupService.createSignupRequest(
			request.getUsername(),
			request.getPassword(),
			request.getName(),
			request.getSlackId()
		);

		PostSignupRequestResDto response = new PostSignupRequestResDto(result);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
	}

	// 회원가입 요청 목록 조회
	@GetMapping
	public ResponseEntity<ApiResponse<GetSignupRequestListResDto>> getSignupRequestList(
		@RequestHeader("X-User-Role") UserRole userRole,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	){
		Pageable pageable = PageRequest.of(page, size);

		GetSignupRequestResult result = signupService.getSignupRequestList(userRole, pageable);
		GetSignupRequestListResDto response = new GetSignupRequestListResDto(result.getContent(), result.getPage(),
			result.getSize(), result.getTotalCount());

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}

	//회원가입요청 처리
	@PatchMapping("/{userId}")
	public ResponseEntity<ApiResponse<PatchSignupRequestResDto>> patchSignupRequest(
		@RequestHeader("X-User-Role") UserRole userRole,
		@PathVariable UUID userId,
		@RequestBody PatchSignupRequestReqDto request
	) {
		PatchSignupRequestResult result = signupService.patchSignupRequest(userRole, userId, request.getStatus(), request.getRole());
		PatchSignupRequestResDto response = new PatchSignupRequestResDto(result);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}
}
