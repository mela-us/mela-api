package com.hcmus.mela.topic.strategy;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.exception.TopicException;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("TOPIC_CONTRIBUTOR")
@RequiredArgsConstructor
public class TopicFilterForContributorStrategy implements TopicFilterStrategy {

    private final TopicRepository topicRepository;

    @Override
    public List<TopicDto> getTopics(UUID userId) {
        List<Topic> verifiedTopics = topicRepository.findAllByStatus(ContentStatus.VERIFIED);
        List<Topic> pendingTopics = topicRepository.findAllByStatusAndCreatedBy(ContentStatus.PENDING, userId);
        List<Topic> deniedTopics = topicRepository.findAllByStatusAndCreatedBy(ContentStatus.DENIED, userId);
        // Combine all topics
        verifiedTopics.addAll(pendingTopics);
        verifiedTopics.addAll(deniedTopics);
        if (verifiedTopics.isEmpty()) {
            return List.of();
        }
        return verifiedTopics.stream()
                .map(TopicMapper.INSTANCE::topicToTopicDto)
                .toList();
    }

    @Override
    public void updateTopic(UUID userId, UUID topicId, UpdateTopicRequest updateTopicRequest) {
        Topic topic = topicRepository.findByTopicIdAndCreatedBy(topicId, userId)
                .orElseThrow(() -> new TopicException("Topic of the contributor not found"));
        if (topic.getStatus() == ContentStatus.DELETED || topic.getStatus() == ContentStatus.VERIFIED) {
            throw new TopicException("Contributor cannot update a deleted or verified topic");
        }
        if (updateTopicRequest.getName() != null && !updateTopicRequest.getName().isEmpty()) {
            topic.setName(updateTopicRequest.getName());
        }
        if (updateTopicRequest.getImageUrl() != null && !updateTopicRequest.getImageUrl().isEmpty()) {
            topic.setImageUrl(updateTopicRequest.getImageUrl());
        }
        topic.setStatus(ContentStatus.PENDING);
        topic.setRejectedReason(null);
        topicRepository.save(topic);
    }
}