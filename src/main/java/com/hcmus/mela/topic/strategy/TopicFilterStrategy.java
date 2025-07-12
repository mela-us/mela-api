package com.hcmus.mela.topic.strategy;

import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;

import java.util.List;
import java.util.UUID;

public interface TopicFilterStrategy {
    List<TopicDto> getTopics(UUID userId);

    void updateTopic(UUID userId, UUID topicId, UpdateTopicRequest request);

    void deleteTopic(UUID userId, UUID topicId);
}