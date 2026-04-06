package com.shipflow.userservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;

public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsBySlackId(String slackId);
	boolean existsByUsername(String username);
	Optional<User> findByIdAndDeletedAtIsNull(UUID userId);
	Optional<User> findByUsername(String username);

	Page<User> findAllByStatus(UserStatus userStatus, Pageable pageable);

	@Query("""
    SELECT u FROM User u
    WHERE u.deletedAt IS NULL
    AND (:role IS NULL OR u.role = :role)
    AND (:status IS NULL OR u.status = :status)
    AND (
        :keyword IS NULL OR
        u.username LIKE %:keyword% OR
        u.name LIKE %:keyword%
    )
    """)
	Page<User> searchUsers(
		UserRole role,
		UserStatus status,
		String keyword,
		Pageable pageable
	);
}