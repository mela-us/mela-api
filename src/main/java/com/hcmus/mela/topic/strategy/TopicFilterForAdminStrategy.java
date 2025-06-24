package com.hcmus.mela.topic.strategy;

import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import com.hcmus.mela.shared.exception.BadRequestException;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("TOPIC_ADMIN")
@RequiredArgsConstructor
public class TopicFilterForAdminStrategy implements TopicFilterStrategy {

    private final TopicRepository topicRepository;

    @Override
    public List<TopicDto> getTopics(UUID userId) {
        List<Topic> topics = topicRepository.findAll();
        if (topics.isEmpty()) {
            return List.of();
        }
        return topics.stream()
                .map(TopicMapper.INSTANCE::topicToTopicDto)
                .toList();
    }

    @Override
    public void updateTopic(UUID userId, UUID topicId, UpdateTopicRequest updateTopicRequest) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new BadRequestException("Topic not found"));
        if (topic.getStatus() == ContentStatus.DELETED) {
            throw new BadRequestException("Cannot update a deleted topic");
        }
        if (updateTopicRequest.getName() != null && !updateTopicRequest.getName().isEmpty()) {
            topic.setName(updateTopicRequest.getName());
        }
        if (updateTopicRequest.getImageUrl() != null && !updateTopicRequest.getImageUrl().isEmpty()) {
            topic.setImageUrl(updateTopicRequest.getImageUrl());
        }
        topicRepository.save(topic);
    }
}