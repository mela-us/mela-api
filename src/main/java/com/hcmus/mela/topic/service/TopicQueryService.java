package com.hcmus.mela.topic.service;

import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.response.GetTopicsResponse;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;

import java.util.List;
import java.util.UUID;

public interface TopicQueryService {

    GetTopicsResponse getTopicsResponse(TopicFilterStrategy topicFilterStrategy, UUID userId);

    List<TopicDto> getTopics();
}
