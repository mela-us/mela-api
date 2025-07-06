package com.hcmus.mela.user.repository;

import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.model.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<User, UUID> {

    Optional<User> findByUserId(UUID userId);

    boolean existsByUsername(String username);

    void updateAllByLevelId(UUID levelId, UUID newLevelId);

    List<User> findAllByUserRole(UserRole userRole);
}
