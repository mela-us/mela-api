package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.shared.type.ContentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LectureRepository extends MongoRepository<Lecture, UUID>, LectureCustomRepository {

    List<Lecture> findAllByStatus(ContentStatus status);

    List<Lecture> findAllByStatusAndCreatedBy(ContentStatus status, UUID userId);

    Optional<Lecture> findByLectureIdAndCreatedBy(UUID lectureId, UUID creatorId);

    Lecture findByLectureId(UUID lectureId);

    @Query("{ 'lectureId': { '$in': ?0 } }")
    List<Lecture> findAllByLectureIdList(List<UUID> lectureIdList);

    Lecture findByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber);
}
