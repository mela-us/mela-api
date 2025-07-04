package com.hcmus.mela.topic.service;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.CreateTopicRequest;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.dto.response.CreateTopicResponse;
import com.hcmus.mela.topic.dto.response.GetTopicsResponse;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;

import java.util.List;
import java.util.UUID;

public interface TopicQueryService {

    GetTopicsResponse getTopicsResponse(TopicFilterStrategy topicFilterStrategy, UUID userId);

    boolean checkTopicStatus(UUID topicId, ContentStatus status);

    List<TopicDto> getTopics();

    List<TopicDto> getVerifiedTopics();

    TopicDto getTopicById(UUID topicId);
}
