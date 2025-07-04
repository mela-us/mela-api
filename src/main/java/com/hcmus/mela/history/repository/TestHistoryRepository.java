package com.hcmus.mela.history.repository;

import com.hcmus.mela.history.model.TestHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface TestHistoryRepository extends MongoRepository<TestHistory, UUID> {
    List<TestHistory> findAllByUserIdAndLevelId(UUID userId, UUID levelId);
}
