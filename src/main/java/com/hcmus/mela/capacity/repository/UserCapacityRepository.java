package com.hcmus.mela.capacity.repository;

import com.hcmus.mela.capacity.model.UserCapacity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface UserCapacityRepository extends MongoRepository<UserCapacity, UUID>, UserCapacityCustomRepository {
    List<UserCapacity> findAllByUserIdAndLevelId(UUID userId, UUID levelId);

    UserCapacity findByUserIdAndLevelIdAndTopicId(UUID userId, UUID levelId, UUID topicId);
}
