package com.hcmus.mela.level.repository;

import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.shared.type.ContentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LevelRepository extends MongoRepository<Level, UUID> {

    List<Level> findAllByStatus(ContentStatus status);

    List<Level> findAllByStatusAndCreatedBy(ContentStatus status, UUID userId);

    Optional<Level> findByName(String levelTitle);

    Optional<Level> findByLevelIdAndCreatedBy(UUID levelId, UUID userId);

    Optional<Level> findByLevelIdAndStatus(UUID levelId, ContentStatus status);

    Optional<Level> findFirstByStatus(ContentStatus status);

    @Query("{ 'createdBy' : ?0 }")
    @Update("{ '$set' : { 'createdBy' : ?1 } }")
    void updateAllByCreatedBy(UUID oldCreatedBy, UUID newCreatedBy);
}
