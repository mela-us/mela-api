package com.hcmus.mela.topic.strategy;

import com.hcmus.mela.lecture.strategy.LectureFilterForAdminStrategy;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.exception.TopicException;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import com.hcmus.mela.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component("TOPIC_ADMIN")
@RequiredArgsConstructor
public class TopicFilterForAdminStrategy implements TopicFilterStrategy {

    private final TopicRepository topicRepository;
    private final LectureFilterForAdminStrategy lectureFilterStrategy;
    private final UserInfoService userInfoService;

    @Override
    public List<TopicDto> getTopics(UUID userId) {
        List<Topic> topics = topicRepository.findAll();
        if (topics.isEmpty()) {
            return List.of();
        }
        return topics.stream()
                .filter(topic -> topic.getStatus() != ContentStatus.DELETED)
                .map(topic -> {
                    TopicDto topicDto = TopicMapper.INSTANCE.topicToTopicDto(topic);
                    if (topic.getCreatedBy() != null) {
                        topicDto.setCreator(userInfoService.getUserPreviewDtoByUserId(topic.getCreatedBy()));
                    }
                    return topicDto;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public TopicDto createTopic(UUID userId, Topic topic) {
        if (topic.getTopicId() == null) {
            topic.setTopicId(UUID.randomUUID());
        }
        topic.setCreatedBy(null);
        if (topic.getStatus() == null) {
            topic.setStatus(ContentStatus.PENDING);
        }
        Topic savedTopic = topicRepository.save(topic);
        return TopicMapper.INSTANCE.topicToTopicDto(savedTopic);
    }

    @Override
    public void updateTopic(UUID userId, UUID topicId, UpdateTopicRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicException("Topic not found in the system"));
        if (topic.getStatus() == ContentStatus.DELETED) {
            throw new TopicException("Cannot update a deleted topic");
        }
        if (request.getName() != null && !request.getName().isEmpty()) {
            topic.setName(request.getName());
        }
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            topic.setImageUrl(request.getImageUrl());
        }
        if (topic.getStatus() == ContentStatus.DENIED) {
            topic.setStatus(ContentStatus.PENDING);
            topic.setRejectedReason(null);
        }
        topicRepository.save(topic);
    }

    @Override
    public void deleteTopic(UUID userId, UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicException("Topic not found in the system"));
        if (topic.getStatus() == ContentStatus.DELETED) {
            return;
        }
        lectureFilterStrategy.deleteLecturesByTopic(userId, topicId);
        topic.setStatus(ContentStatus.DELETED);
        topicRepository.save(topic);
    }
}