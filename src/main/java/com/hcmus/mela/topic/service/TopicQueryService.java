package com.hcmus.mela.topic.service;

import com.hcmus.mela.topic.dto.response.GetTopicsResponse;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;

import java.util.UUID;

public interface TopicQueryService {

    GetTopicsResponse getTopicsResponse(TopicFilterStrategy topicFilterStrategy, UUID userId);
}
