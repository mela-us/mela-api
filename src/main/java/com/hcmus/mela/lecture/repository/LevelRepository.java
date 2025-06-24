package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Level;
import com.hcmus.mela.lecture.model.Topic;
import com.hcmus.mela.shared.type.ContentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface LevelRepository extends MongoRepository<Level, UUID> {
    List<Level> findAllByStatus(ContentStatus status);

    List<Level> findAllByStatusAndCreatedBy(ContentStatus status, UUID userId);

    Level findByName(String levelTitle);
}
