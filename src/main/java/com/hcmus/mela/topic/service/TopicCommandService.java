package com.hcmus.mela.topic.service;

import com.hcmus.mela.topic.dto.request.CreateTopicRequest;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.dto.response.CreateTopicResponse;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;

import java.util.UUID;

public interface TopicCommandService {

    CreateTopicResponse createTopic(TopicFilterStrategy strategy, UUID userId, CreateTopicRequest request);

    void updateTopic(TopicFilterStrategy strategy, UUID userId, UUID topicId, UpdateTopicRequest request);

    void deleteTopic(TopicFilterStrategy strategy, UUID userId, UUID topicId);
}
