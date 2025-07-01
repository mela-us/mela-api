package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.exercise.strategy.ExerciseFilterForContributorStrategy;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.lecture.exception.LectureException;
import com.hcmus.mela.lecture.mapper.LectureMapper;
import com.hcmus.mela.lecture.mapper.LectureSectionMapper;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.repository.LectureRepository;
import com.hcmus.mela.level.service.LevelService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("LECTURE_CONTRIBUTOR")
@RequiredArgsConstructor
public class LectureFilterForContributorStrategy implements LectureFilterStrategy {

    private final LectureRepository lectureRepository;

    private final TopicService topicService;

    private final LevelService levelService;

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
        if (lecture.getTopicId() == null || topicService.isTopicAssignableToLecture(lecture.getLectureId(), userId)) {
            throw new LectureException("Topic is not assignable to this lecture");
        }
        if (lecture.getLevelId() == null || levelService.isLevelAssignableToLecture(lecture.getLevelId(), userId)) {
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
    public void updateLecture(UUID userId, UUID lectureId, UpdateLectureRequest updateLectureRequest) {
        Lecture lecture = lectureRepository.findByLectureIdAndCreatedBy(lectureId, userId)
                .orElseThrow(() -> new LectureException("Lecture of the contributor not found"));
        if (lecture.getStatus() == ContentStatus.DELETED || lecture.getStatus() == ContentStatus.VERIFIED) {
            throw new LectureException("Contributor cannot update a deleted or verified lecture");
        }
        if (updateLectureRequest.getName() != null && !updateLectureRequest.getName().isEmpty()) {
            lecture.setName(updateLectureRequest.getName());
        }
        if (updateLectureRequest.getOrdinalNumber() != null && updateLectureRequest.getOrdinalNumber() > 0) {
            lecture.setOrdinalNumber(updateLectureRequest.getOrdinalNumber());
        }
        if (updateLectureRequest.getDescription() != null && !updateLectureRequest.getDescription().isEmpty()) {
            lecture.setDescription(updateLectureRequest.getDescription());
        }
        if (!updateLectureRequest.getSections().isEmpty()) {
            lecture.setSections(updateLectureRequest.getSections().stream().map(LectureSectionMapper.INSTANCE::updateSectionRequestToSection).toList());
        }
        if (topicService.isTopicAssignableToLecture(updateLectureRequest.getTopicId(), userId)) {
            lecture.setTopicId(updateLectureRequest.getTopicId());
        } else {
            throw new LectureException("Topic is not assignable to this lecture");
        }
        if (levelService.isLevelAssignableToLecture(updateLectureRequest.getLevelId(), userId)) {
            lecture.setLevelId(updateLectureRequest.getLevelId());
        } else {
            throw new LectureException("Level is not assignable to this lecture");
        }
        lecture.setStatus(ContentStatus.PENDING);
        lecture.setRejectedReason(null);
        lectureRepository.save(lecture);
    }
}