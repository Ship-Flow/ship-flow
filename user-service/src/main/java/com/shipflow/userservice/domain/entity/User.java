package com.shipflow.userservice.domain.entity;

import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;
import com.shipflow.common.exception.BusinessException;
import com.shipflow.userservice.domain.error.UserErrorCode;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@Column(nullable = false)
	private UUID id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "slack_id", nullable = false, unique = true, length = 100)
	private String slackId;

	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private UserRole role;

	@Column(name = "hub_id")
	private UUID hubId;

	@Column(name = "company_id")
	private UUID companyId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserStatus status;

	public User(UUID id, String username, String name, String slackId) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.slackId = slackId;
		this.status = UserStatus.PENDING;
	}

	public void approve(UserRole role){ //승인
		if (this.status != UserStatus.PENDING) {
			throw new BusinessException(UserErrorCode.INVALID_USER_STATUS);
		}

		this.status = UserStatus.APPROVED;
		changeRole(role);
	}
	public void reject(){ //거부
		if (this.status != UserStatus.PENDING) {
			throw new BusinessException(UserErrorCode.INVALID_USER_STATUS);
		}

		this.status = UserStatus.REJECTED;
	}

	public void changeRole(UserRole role) {this.role = role;} // 권한 변경
	public void changeName(String name) { this.name = name;	} // 이름 변경
	public void changeSlackId(String slackId) { this.slackId = slackId; } // 슬랙 id 변경
	public void changeHubId(UUID hubId) { this.hubId = hubId; } // 담당 허브 id 변경
	public void changeCompanyId(UUID companyId) { this.companyId = companyId;} //담당 업체 id 변경

	public void softDeleted(UUID deletedBy){ super.softDelete(deletedBy);} //논리적 삭제

	public void updateMyInfo(String name, String slackId) {
		changeName(name);
		changeSlackId(slackId);
	}
}

