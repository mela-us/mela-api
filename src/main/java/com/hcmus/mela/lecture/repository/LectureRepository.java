package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.shared.type.ContentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LectureRepository extends MongoRepository<Lecture, UUID>, LectureCustomRepository {

    List<Lecture> findAllByStatus(ContentStatus status);

    List<Lecture> findAllByStatusAndLevelId(ContentStatus status, UUID levelId);

    List<Lecture> findAllByStatusAndCreatedBy(ContentStatus status, UUID userId);

    List<Lecture> findAllByStatusAndCreatedByAndLevelId(ContentStatus status, UUID userId, UUID levelId);

    Optional<Lecture> findByLectureIdAndCreatedBy(UUID lectureId, UUID creatorId);

    Lecture findByLectureId(UUID lectureId);

    Optional<Lecture> findByLectureIdAndStatus(UUID lectureId, ContentStatus status);

    Lecture findByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber);

    List<Lecture> findAllByTopicId(UUID topicId);

    List<Lecture> findAllByTopicIdAndCreatedBy(UUID topicId, UUID createdBy);

    List<Lecture> findAllByLevelId(UUID levelId);

    List<Lecture> findAllByLevelIdAndCreatedBy(UUID levelId, UUID createdBy);

    @Query("{ 'createdBy' : ?0 }")
    @Update("{ '$set' : { 'createdBy' : ?1 } }")
    void updateAllByCreatedBy(UUID oldCreatedBy, UUID newCreatedBy);

    Integer countAllByCreatedBy(UUID userId);

    Integer countAllByCreatedByAndStatus(UUID userId, ContentStatus status);

    List<Lecture> findAllByCreatedBy(UUID userId);

    Integer countAllByTopicIdAndStatusAndCreatedBy(UUID topicId, ContentStatus status, UUID createdBy);

    Integer countAllByTopicIdAndCreatedBy(UUID topicId, UUID createdBy);
}
