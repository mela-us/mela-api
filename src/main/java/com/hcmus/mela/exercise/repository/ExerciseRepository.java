package com.hcmus.mela.exercise.repository;

import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.shared.type.ContentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExerciseRepository extends MongoRepository<Exercise, UUID>, ExerciseCustomRepository {

    Optional<Exercise> findByExerciseIdAndStatus(UUID exerciseId, ContentStatus status);

    Optional<Exercise> findByQuestionsQuestionIdAndStatus(UUID questionId, ContentStatus status);

    Optional<Exercise> findByExerciseIdAndCreatedBy(UUID exerciseId, UUID creatorId);

    List<Exercise> findAllByLectureId(UUID lectureId);

    List<Exercise> findAllByLectureIdAndStatus(UUID lectureId, ContentStatus status);

    List<Exercise> findAllByLectureIdAndCreatedBy(UUID lectureId, UUID createdBy);

    List<Exercise> findAllByStatusAndCreatedBy(ContentStatus status, UUID userId);

    List<Exercise> findAllByStatus(ContentStatus status);

    void updateAllByCreatedBy(UUID previousUserId, UUID newUserId);
}
