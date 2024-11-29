package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Topic;
import org.bson.types.Binary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface TopicRepository extends MongoRepository<Topic, UUID> {
}
