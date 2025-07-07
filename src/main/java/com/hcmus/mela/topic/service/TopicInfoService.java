package com.hcmus.mela.topic.service;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.dto.dto.TopicDto;

import java.util.List;
import java.util.UUID;

public interface TopicInfoService {

    List<TopicDto> findAllTopics();

    List<TopicDto> findAllTopicsInStatus(ContentStatus status);

    TopicDto findTopicByTopicId(UUID topicId);

    TopicDto findTopicByTopicIdAndStatus(UUID topicId, ContentStatus status);

    void changeTopicOwnerToAdmin(UUID previousUserId);
}
