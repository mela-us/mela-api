package com.hcmus.mela.topic.repository;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.model.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends MongoRepository<Topic, UUID> {

    List<Topic> findAllByStatus(ContentStatus status);

    List<Topic> findAllByStatusAndCreatedBy(ContentStatus status, UUID userId);

    Optional<Topic> findByTopicIdAndCreatedBy(UUID topicId, UUID userId);

    Optional<Topic> findByTopicIdAndStatus(UUID topicId, ContentStatus status);

    @Query("{ 'createdBy' : ?0 }")
    @Update("{ '$set' : { 'createdBy' : ?1 } }")
    void updateAllByCreatedBy(UUID oldCreatedBy, UUID newCreatedBy);
}
