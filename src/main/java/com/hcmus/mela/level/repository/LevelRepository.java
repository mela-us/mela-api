package com.hcmus.mela.level.repository;

import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.shared.type.ContentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LevelRepository extends MongoRepository<Level, UUID> {
    List<Level> findAllByStatus(ContentStatus status);

    List<Level> findAllByStatusAndCreatedBy(ContentStatus status, UUID userId);

    Optional<Level> findByLevelIdAndCreatedBy(UUID levelId, UUID creatorId);

    Level findByName(String levelTitle);
}
