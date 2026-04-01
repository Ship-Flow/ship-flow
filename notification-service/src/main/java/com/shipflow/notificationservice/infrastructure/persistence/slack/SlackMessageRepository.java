package com.shipflow.notificationservice.infrastructure.persistence.slack;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shipflow.notificationservice.domain.slack.SlackMessage;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, UUID> {

	// TODO: 단건 조회 시 soft delete 제외
	Optional<SlackMessage> findByIdAndDeletedAtIsNull(UUID id);

	// TODO: 목록 조회는 추후 검색조건(receiverSlackId, sendStatus, messageType, relatedShipmentId 등) 추가 시
	// TODO: Specification / Querydsl / custom repository 방식으로 확장
	Page<SlackMessage> findAllByDeletedAtIsNull(Pageable pageable);

}