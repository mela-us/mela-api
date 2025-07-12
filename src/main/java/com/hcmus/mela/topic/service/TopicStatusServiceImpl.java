package com.hcmus.mela.topic.service;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.exception.TopicException;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicStatusServiceImpl implements TopicStatusService {

    private final TopicRepository topicRepository;

    @Override
    public void denyTopic(UUID topicId, String reason) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicException("Topic not found in the system"));
        if (topic.getStatus() == ContentStatus.VERIFIED || topic.getStatus() == ContentStatus.DELETED) {
            throw new TopicException("Verified or deleted topic cannot be denied");
        }
        topic.setRejectedReason(reason);
        topic.setStatus(ContentStatus.DENIED);
        topicRepository.save(topic);
    }

    @Override
    public void approveTopic(UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicException("Topic not found in the system"));
        if (topic.getStatus() == ContentStatus.DELETED) {
            throw new TopicException("Deleted topic cannot be approved");
        }
        topic.setRejectedReason(null);
        topic.setStatus(ContentStatus.VERIFIED);
        topicRepository.save(topic);
    }

    @Override
    public boolean isTopicAssignableToLecture(UUID userId, UUID topicId) {
        if (topicId == null || userId == null) {
            return false;
        }
        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null) {
            return false;
        }
        if (topic.getStatus() == ContentStatus.VERIFIED) {
            return true;
        }
        return topic.getStatus() != ContentStatus.DELETED && userId.equals(topic.getCreatedBy());
    }

    @Override
    public boolean isTopicInStatus(UUID topicId, ContentStatus status) {
        if (topicId == null || status == null) {
            return false;
        }
        Topic topic = topicRepository.findById(topicId).orElse(null);
        return topic != null && topic.getStatus() == status;
    }
}
