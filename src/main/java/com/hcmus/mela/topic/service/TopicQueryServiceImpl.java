package com.hcmus.mela.topic.service;

import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.response.GetTopicsResponse;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicQueryServiceImpl implements TopicQueryService {

    public GetTopicsResponse getTopicsResponse(TopicFilterStrategy strategy, UUID userId) {
        List<TopicDto> topics = strategy.getTopics(userId);
        if (topics.isEmpty()) {
            return new GetTopicsResponse("No topics found in the system", 0, Collections.emptyList());
        }
        return new GetTopicsResponse("Topics retrieved successfully", topics.size(), topics);
    }
}
