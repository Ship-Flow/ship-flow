package com.shipflow.userservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;

public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsBySlackId(String slackId);
	boolean existsByUsername(String username);

	Page<User> findAllByStatus(UserStatus userStatus, Pageable pageable);
	Page<User> findAllByRoleAndStatus(UserRole role, UserStatus status, Pageable pageable);
	Page<User> findAllByRole(UserRole role, Pageable pageable);

	Optional<User> findByUsername(String username);
}