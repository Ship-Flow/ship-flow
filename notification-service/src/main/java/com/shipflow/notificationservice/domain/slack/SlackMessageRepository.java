package com.shipflow.notificationservice.domain.slack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SlackMessageRepository {

	SlackMessage save(SlackMessage slackMessage);

	Optional<SlackMessage> findByIdAndDeletedAtIsNull(UUID slackId);

	List<SlackMessage> findAllByDeletedAtIsNull();
}