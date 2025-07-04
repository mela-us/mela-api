package com.hcmus.mela.skills.repository;

import com.hcmus.mela.skills.model.UserSkill;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSkillRepository extends MongoRepository<UserSkill, UUID>, UserSkillCustomRepository {

    List<UserSkill> findAllByUserId(UUID userId);

    List<UserSkill> findAllByUserIdAndLevelId(UUID userId, UUID levelId);

    Optional<UserSkill> findByUserIdAndLevelIdAndTopicId(UUID userId, UUID levelId, UUID topicId);
}
