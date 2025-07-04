package com.hcmus.mela.user.repository;

import com.hcmus.mela.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<User, UUID> {
    Optional<User> findByUserId(UUID userId);
}
