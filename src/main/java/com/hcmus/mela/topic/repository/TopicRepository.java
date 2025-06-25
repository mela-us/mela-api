package com.hcmus.mela.topic.repository;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.model.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends MongoRepository<Topic, UUID> {
    List<Topic> findAllByStatus(ContentStatus status);

    List<Topic> findAllByStatusAndCreatedBy(ContentStatus status, UUID userId);

    Optional<Topic> findByTopicIdAndCreatedBy(UUID topicId, UUID creatorId);

    Topic findByTopicId(UUID topicId);
}
