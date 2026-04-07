package com.shipflow.notificationservice.infrastructure.persistence.slack;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shipflow.notificationservice.domain.slack.SlackMessage;

public interface SlackMessageJpaRepository extends JpaRepository<SlackMessage, UUID> {

	Optional<SlackMessage> findByIdAndDeletedAtIsNull(UUID id);

}
