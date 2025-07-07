package com.hcmus.mela.test.repository;

import com.hcmus.mela.test.model.TestQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestQuestionRepository extends MongoRepository<TestQuestion, UUID> {

    List<TestQuestion> findAllByLevelId(UUID levelId);

    TestQuestion findByQuestionsQuestionId(UUID questionsQuestionId);
}
