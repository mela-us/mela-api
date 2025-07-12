package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.exercise.strategy.ExerciseFilterForContributorStrategy;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.lecture.exception.LectureException;
import com.hcmus.mela.lecture.mapper.LectureMapper;
import com.hcmus.mela.lecture.mapper.LectureSectionMapper;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.repository.LectureRepository;
import com.hcmus.mela.level.service.LevelStatusService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.service.TopicStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("LECTURE_CONTRIBUTOR")
@RequiredArgsConstructor
public class LectureFilterForContributorStrategy implements LectureFilterStrategy {

    private final LectureRepository lectureRepository;
    private final LevelStatusService levelStatusService;
    private final TopicStatusService topicStatusService;
    private final ExerciseFilterForContributorStrategy exerciseFilterForContributorStrategy;

    @Override
    public List<LectureDto> getLectures(UUID userId) {
        List<Lecture> verifiedLectures = lectureRepository.findAllByStatus(ContentStatus.VERIFIED);
        List<Lecture> pendingLectures = lectureRepository.findAllByStatusAndCreatedBy(ContentStatus.PENDING, userId);
        List<Lecture> deniedLectures = lectureRepository.findAllByStatusAndCreatedBy(ContentStatus.DENIED, userId);
        // Combine all lectures
        verifiedLectures.addAll(pendingLectures);
        verifiedLectures.addAll(deniedLectures);
        if (verifiedLectures.isEmpty()) {
            return List.of();
        }
        return verifiedLectures.stream()
                .map(LectureMapper.INSTANCE::lectureToLectureDto)
                .toList();
    }

    @Override
    public LectureDto createLecture(UUID userId, Lecture lecture) {
        if (lecture.getTopicId() == null || topicStatusService.isTopicAssignableToLecture(userId, lecture.getLectureId())) {
            throw new LectureException("Topic is not assignable to this lecture");
        }
        if (lecture.getLevelId() == null || levelStatusService.isLevelAssignableToLecture(userId, lecture.getLevelId())) {
            throw new LectureException("Level is not assignable to this lecture");
        }
        Lecture savedLecture = lectureRepository.save(lecture);
        return LectureMapper.INSTANCE.lectureToLectureDto(savedLecture);
    }

    @Override
    public LectureDto getLectureById(UUID userId, UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found"));
        if (lecture.getStatus() == ContentStatus.DELETED) {
            throw new LectureException("Lecture has been deleted");
        }
        if (lecture.getCreatedBy().equals(userId) || lecture.getStatus() == ContentStatus.VERIFIED) {
            return LectureMapper.INSTANCE.lectureToLectureDto(lecture);
        }
        throw new LectureException("Contributor cannot view this lecture");
    }

    @Override
    public void deleteLecture(UUID userId, UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureIdAndCreatedBy(lectureId, userId)
                .orElseThrow(() -> new LectureException("Lecture of the contributor not found"));
        if (lecture.getStatus() == ContentStatus.VERIFIED) {
            throw new LectureException("Contributor cannot delete a verified lecture");
        }
        exerciseFilterForContributorStrategy.deleteExercisesByLecture(userId, lectureId);
        lecture.setStatus(ContentStatus.DELETED);
        lectureRepository.save(lecture);
    }

    @Override
    public void deleteLecturesByTopic(UUID userId, UUID topicId) {
        List<Lecture> lectures = lectureRepository.findAllByTopicIdAndCreatedBy(topicId, userId);
        if (lectures.isEmpty()) {
            return;
        }
        for (Lecture lecture : lectures) {
            exerciseFilterForContributorStrategy.deleteExercisesByLecture(userId, lecture.getLectureId());
            lecture.setStatus(ContentStatus.DELETED);
            lectureRepository.save(lecture);
        }

    }

    @Override
    public void deleteLecturesByLevel(UUID userId, UUID levelId) {
        List<Lecture> lectures = lectureRepository.findAllByLevelIdAndCreatedBy(levelId, userId);
        if (lectures.isEmpty()) {
            return;
        }
        for (Lecture lecture : lectures) {
            exerciseFilterForContributorStrategy.deleteExercisesByLecture(userId, lecture.getLectureId());
            lecture.setStatus(ContentStatus.DELETED);
            lectureRepository.save(lecture);
        }
    }

    @Override
    public void updateLecture(UUID userId, UUID lectureId, UpdateLectureRequest request) {
        Lecture lecture = lectureRepository.findByLectureIdAndCreatedBy(lectureId, userId)
                .orElseThrow(() -> new LectureException("Lecture of the contributor not found"));
        if (lecture.getStatus() == ContentStatus.DELETED || lecture.getStatus() == ContentStatus.VERIFIED) {
            throw new LectureException("Contributor cannot update a deleted or verified lecture");
        }
        if (request.getName() != null && !request.getName().isEmpty()) {
            lecture.setName(request.getName());
        }
        if (request.getOrdinalNumber() != null && request.getOrdinalNumber() > 0) {
            lecture.setOrdinalNumber(request.getOrdinalNumber());
        }
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            lecture.setDescription(request.getDescription());
        }
        if (!request.getSections().isEmpty()) {
            lecture.setSections(request.getSections().stream().map(LectureSectionMapper.INSTANCE::updateSectionRequestToSection).toList());
        }
        if (topicStatusService.isTopicAssignableToLecture(userId, request.getTopicId())) {
            lecture.setTopicId(request.getTopicId());
        } else {
            throw new LectureException("Topic is not assignable to this lecture");
        }
        if (levelStatusService.isLevelAssignableToLecture(userId, request.getLevelId())) {
            lecture.setLevelId(request.getLevelId());
        } else {
            throw new LectureException("Level is not assignable to this lecture");
        }
        lecture.setStatus(ContentStatus.PENDING);
        lecture.setRejectedReason(null);
        lectureRepository.save(lecture);
    }
}