package com.hcmus.mela.topic.service;

import com.hcmus.mela.shared.type.ContentStatus;

import java.util.UUID;

public interface TopicStatusService {

    void denyTopic(UUID topicId, String reason);

    void approveTopic(UUID topicId);

    boolean isTopicInStatus(UUID topicId, ContentStatus status);

    boolean isTopicAssignableToLecture(UUID userId, UUID topicId);
}
