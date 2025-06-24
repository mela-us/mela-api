package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.dto.request.CreateTopicRequest;
import com.hcmus.mela.lecture.dto.response.CreateTopicResponse;
import com.hcmus.mela.lecture.dto.response.GetTopicsResponse;
import com.hcmus.mela.lecture.strategy.TopicFilterStrategy;

import java.util.List;
import java.util.UUID;

public interface TopicService {

    GetTopicsResponse getTopicsResponse(TopicFilterStrategy topicFilterStrategy, UUID userId);

    CreateTopicResponse getCreateTopicResponse(UUID creatorId, CreateTopicRequest createTopicRequest);

    List<TopicDto> getTopics();

    TopicDto getTopicById(UUID topicId);
}
