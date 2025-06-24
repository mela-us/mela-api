package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.dto.request.UpdateTopicRequest;

import java.util.List;
import java.util.UUID;

public interface TopicFilterStrategy {
    List<TopicDto> getTopics(UUID userId);

    void updateTopic(UUID userId, UUID topicId, UpdateTopicRequest updateTopicRequest);
}