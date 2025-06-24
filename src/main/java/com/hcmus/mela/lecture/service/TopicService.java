package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.dto.request.CreateTopicRequest;
import com.hcmus.mela.lecture.dto.request.UpdateTopicRequest;
import com.hcmus.mela.lecture.dto.response.CreateTopicResponse;
import com.hcmus.mela.lecture.dto.response.GetTopicsResponse;
import com.hcmus.mela.lecture.strategy.TopicFilterStrategy;

import java.util.List;
import java.util.UUID;

public interface TopicService {

    GetTopicsResponse getTopicsResponse(TopicFilterStrategy topicFilterStrategy, UUID userId);

    CreateTopicResponse getCreateTopicResponse(UUID creatorId, CreateTopicRequest createTopicRequest);

    void updateTopic(TopicFilterStrategy strategy, UUID userId, UUID topicId, UpdateTopicRequest request);

    void denyTopic(UUID topicId, String reason);

    void approveTopic(UUID topicId);

    List<TopicDto> getTopics();

    TopicDto getTopicById(UUID topicId);
}
