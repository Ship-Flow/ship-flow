package com.shipflow.userservice.presentation.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.repository.UserRepository;
import com.shipflow.userservice.presentation.dto.request.LoginReqDto;
import com.shipflow.userservice.presentation.dto.response.LoginResDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final RestTemplate restTemplate;
	private final UserRepository userRepository;

	@Value("${keycloak.server-url}")
	private String serverUrl;

	@Value("${keycloak.user-realm}")
	private String userRealm;

	@Value("${keycloak.login-client-id}")
	private String clientId;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginReqDto request) {
		String tokenUrl = serverUrl + "/realms/" + userRealm + "/protocol/openid-connect/token";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		System.out.println("[UserService] login endpoint called");

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "password");
		form.add("client_id", clientId);
		form.add("username", request.getUsername());
		form.add("password", request.getPassword());
		form.add("scope", "openid");

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);

			User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("사용자 정보 없음"));

			Map<String, Object> tokenBody = response.getBody();

			LoginResDto result = new LoginResDto(
				(String) tokenBody.get("access_token"),
				((Number) tokenBody.get("expires_in")).intValue(),
				user.getId(),
				user.getUsername(),
				user.getRole(),
				user.getSlackId(),
				user.getHubId(),
				user.getCompanyId()
			);

			return ResponseEntity.ok(result);

		} catch (HttpStatusCodeException e) {
			log.error("Keycloak login failed. status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());

			return ResponseEntity.status(e.getStatusCode())
				.body(Map.of(
					"error", "login_failed",
					"details", e.getResponseBodyAsString()
				));
		}
	}
}