package com.shipflow.notificationservice.infrastructure.persistence.slack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shipflow.notificationservice.domain.slack.SlackMessage;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, UUID> {

	// TODO: 단건 조회 시 soft delete 제외
	Optional<SlackMessage> findByIdAndDeletedAtIsNull(UUID id);

	// TODO: 목록 조회 페이징 및 검색 처리 필요
	List<SlackMessage> findAllByDeletedAtIsNull();

}