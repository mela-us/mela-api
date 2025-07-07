package com.hcmus.mela.token.repository;


import com.hcmus.mela.token.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends MongoRepository<Token, UUID> {

    Optional<Token> findByUserId(UUID userId);

    List<Token> findByTokenLessThan(Integer value);
}
