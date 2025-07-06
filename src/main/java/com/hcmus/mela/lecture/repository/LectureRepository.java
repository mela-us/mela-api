package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.shared.type.ContentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LectureRepository extends MongoRepository<Lecture, UUID>, LectureCustomRepository {

    List<Lecture> findAllByStatus(ContentStatus status);

    List<Lecture> findAllByStatusAndCreatedBy(ContentStatus status, UUID userId);

    Optional<Lecture> findByLectureIdAndCreatedBy(UUID lectureId, UUID creatorId);

    Lecture findByLectureId(UUID lectureId);

    Optional<Lecture> findByLectureIdAndStatus(UUID lectureId, ContentStatus status);

    Lecture findByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber);

    List<Lecture> findAllByTopicId(UUID topicId);

    List<Lecture> findAllByTopicIdAndCreatedBy(UUID topicId, UUID createdBy);

    List<Lecture> findAllByLevelId(UUID levelId);

    List<Lecture> findAllByLevelIdAndCreatedBy(UUID levelId, UUID createdBy);

    void updateAllByCreatedBy(UUID previousUserId, UUID newUserId);
}
