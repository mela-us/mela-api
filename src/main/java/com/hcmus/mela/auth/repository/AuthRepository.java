package com.hcmus.mela.auth.repository;

import com.hcmus.mela.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthRepository extends MongoRepository<User, UUID> {

    User findByUsername(String username);

    boolean existsByUsername(String username);
}
