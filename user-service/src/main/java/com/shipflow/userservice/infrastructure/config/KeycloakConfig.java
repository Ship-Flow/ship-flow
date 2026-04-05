package com.shipflow.userservice.infrastructure.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

	@Value("${keycloak.server-url}") private String serverUrl;
	@Value("${keycloak.realm}") private String realm;
	@Value("${keycloak.admin-client-id}") private String clientId;
	@Value("${keycloak.admin-username}") private String adminUsername;
	@Value("${keycloak.admin-password}") private String adminPassword;

	@Bean
	public Keycloak keycloakAdminClient() {
		return KeycloakBuilder.builder()
			.serverUrl(serverUrl)
			.realm(realm) // 보통 admin 로그인은 master realm
			.clientId(clientId)
			.grantType(OAuth2Constants.PASSWORD)
			.username(adminUsername)
			.password(adminPassword)
			.build();
	}
}
