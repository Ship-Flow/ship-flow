package com.shipflow.userservice.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shipflow.userservice.domain.model.UserRole;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

	private final Keycloak keycloak;

	@Value("${keycloak.user-realm}")
	private String userRealm;

	public UUID createPendingUser(String username, String password) { //사용자 생성
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(password);
		credential.setTemporary(false);

		UserRepresentation user = new UserRepresentation();
		user.setUsername(username);
		user.setEnabled(false); // 승인 전에는 비활성화
		user.setCredentials(List.of(credential));

		Response response = keycloak.realm(userRealm).users().create(user);

		if (response.getStatus() != 201) {
			String errorBody = response.readEntity(String.class);
			throw new IllegalStateException(
				"Keycloak 사용자 생성 실패. body=" + errorBody
			);
		}

		String location = response.getHeaderString("Location");
		String userId = location.substring(location.lastIndexOf('/') + 1);

		//자동생성되는 Role 삭제
		RealmResource realmResource = keycloak.realm(userRealm);
		RoleRepresentation defaultRole = realmResource.roles()
			.get("default-roles-shipflow")
			.toRepresentation();

		realmResource.users().get(userId)
			.roles().realmLevel()
			.remove(List.of(defaultRole));

		return UUID.fromString(userId);
	}

	public void deleteUser(UUID userId) { //사용자 제거
		String keycloakId = userId.toString();
		keycloak.realm(userRealm)
			.users()
			.delete(keycloakId);
	}

	public void setUserRole(UUID userId, UserRole grantRole) { //역할부여
		RealmResource realmResource = keycloak.realm(userRealm);
		UserResource userResource = realmResource.users().get(userId.toString());

		RoleRepresentation role = realmResource.roles()
			.get(grantRole.name())
			.toRepresentation();

		userResource.roles()
			.realmLevel()
			.add(List.of(role));
	}

	public void enableUser(UUID userId) { //사용자 활성화
		UserResource userResource = keycloak.realm(userRealm)
			.users()
			.get(userId.toString());

		UserRepresentation user = userResource.toRepresentation();
		user.setEnabled(true);
		userResource.update(user);
	}

	public void disableUser(UUID userId){ //사용자 비활성화
		UserResource userResource = keycloak.realm(userRealm)
			.users()
			.get(userId.toString());

		UserRepresentation user = userResource.toRepresentation();
		user.setEnabled(false);
		userResource.update(user);
	}

	public void updateUserRole(UUID userId, UserRole currentRole, UserRole newRole) {
		if (newRole == null || currentRole == newRole) return;

		RealmResource realmResource = keycloak.realm(userRealm);
		UserResource userResource = realmResource.users().get(userId.toString());
		RoleMappingResource roleMappingResource = userResource.roles();

		if (currentRole != null) { //기존 권한 제거
			RoleRepresentation currentRoleRep = realmResource.roles()
				.get(currentRole.name())
				.toRepresentation();

			roleMappingResource.realmLevel().remove(List.of(currentRoleRep));
		}

		//새로운 권한 추가
		RoleRepresentation newRoleRep = realmResource.roles()
			.get(newRole.name())
			.toRepresentation();

		roleMappingResource.realmLevel().add(List.of(newRoleRep));
	}
}