package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.exercise.strategy.ExerciseFilterForAdminStrategy;
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
import com.hcmus.mela.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component("LECTURE_ADMIN")
@RequiredArgsConstructor
public class LectureFilterForAdminStrategy implements LectureFilterStrategy {

    private final LectureRepository lectureRepository;
    private final TopicStatusService topicStatusService;
    private final LevelStatusService levelStatusService;
    private final UserInfoService userInfoService;
    private final ExerciseFilterForAdminStrategy exerciseFilterForAdminStrategy;

    @Override
    public List<LectureDto> getLectures(UUID userId) {
        List<Lecture> lectures = lectureRepository.findAll();
        if (lectures.isEmpty()) {
            return List.of();
        }
        return lectures.stream()
                .filter(lecture -> lecture.getStatus() != ContentStatus.DELETED)
                .map(lecture -> {
                    LectureDto lectureDto = LectureMapper.INSTANCE.lectureToLectureDto(lecture);
                    if (lecture.getCreatedBy() != null) {
                        lectureDto.setCreator(userInfoService.getUserPreviewDtoByUserId(lecture.getCreatedBy()));
                    }
                    return lectureDto;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public LectureDto createLecture(UUID userId, Lecture lecture) {
        if (topicStatusService.isTopicInStatus(lecture.getTopicId(), ContentStatus.DELETED)) {
            throw new LectureException("Deleted topic cannot be assigned to lecture");
        }
        if (levelStatusService.isLevelInStatus(lecture.getLevelId(), ContentStatus.DELETED)) {
            throw new LectureException("Deleted level cannot be assigned to lecture");
        }
        lecture.setLectureId(UUID.randomUUID());
        lecture.setStatus(ContentStatus.PENDING);
        Lecture savedLecture = lectureRepository.save(lecture);
        return LectureMapper.INSTANCE.lectureToLectureDto(savedLecture);
    }

    @Override
    public void updateLecture(UUID userId, UUID lectureId, UpdateLectureRequest request) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found in the system"));
        if (lecture.getStatus() == ContentStatus.DELETED) {
            throw new LectureException("Lecture is deleted and cannot be updated");
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
        if (request.getSections() != null && !request.getSections().isEmpty()) {
            lecture.setSections(request.getSections().stream().map(LectureSectionMapper.INSTANCE::updateSectionRequestToSection).toList());
        }
        if (lecture.getStatus() == ContentStatus.VERIFIED
                && (!topicStatusService.isTopicInStatus(request.getTopicId(), ContentStatus.VERIFIED)
                || !levelStatusService.isLevelInStatus(request.getLevelId(), ContentStatus.VERIFIED))) {
            throw new LectureException("Verified lecture must have verified topic and level");
        }
        if (!topicStatusService.isTopicInStatus(request.getTopicId(), ContentStatus.DELETED)) {
            lecture.setTopicId(request.getTopicId());
        } else {
            throw new LectureException("Deleted topic cannot be assigned to this lecture");
        }
        if (!levelStatusService.isLevelInStatus(request.getLevelId(), ContentStatus.DELETED)) {
            lecture.setLevelId(request.getLevelId());
        } else {
            throw new LectureException("Deleted level cannot be assigned to this lecture");
        }
        if (lecture.getStatus() == ContentStatus.DENIED) {
            lecture.setStatus(ContentStatus.PENDING);
            lecture.setRejectedReason(null);
        }
        lectureRepository.save(lecture);
    }

    public LectureDto getLectureById(UUID userId, UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found in the system"));
        if (lecture.getStatus() == ContentStatus.DELETED) {
            throw new LectureException("Lecture is deleted and cannot be retrieved");
        }
        LectureDto lectureDto = LectureMapper.INSTANCE.lectureToLectureDto(lecture);
        if (lecture.getCreatedBy() != null) {
            lectureDto.setCreator(userInfoService.getUserPreviewDtoByUserId(lecture.getCreatedBy()));
        }
        return lectureDto;
    }

    @Transactional
    @Override
    public void deleteLecture(UUID userId, UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found in the system"));
        if (lecture.getStatus() == ContentStatus.DELETED) {
            return;
        }
        exerciseFilterForAdminStrategy.deleteExercisesByLecture(userId, lectureId);
        lecture.setStatus(ContentStatus.DELETED);
        lectureRepository.save(lecture);
    }

    @Override
    public void deleteLecturesByTopic(UUID userId, UUID topicId) {
        List<Lecture> lectures = lectureRepository.findAllByTopicId(topicId);
        if (lectures.isEmpty()) {
            return;
        }
        for (Lecture lecture : lectures) {
            exerciseFilterForAdminStrategy.deleteExercisesByLecture(userId, lecture.getLectureId());
            lecture.setStatus(ContentStatus.DELETED);
            lectureRepository.save(lecture);
        }
    }

    @Override
    public void deleteLecturesByLevel(UUID userId, UUID levelId) {
        List<Lecture> lectures = lectureRepository.findAllByLevelId(levelId);
        if (lectures.isEmpty()) {
            return;
        }
        for (Lecture lecture : lectures) {
            exerciseFilterForAdminStrategy.deleteExercisesByLecture(userId, lecture.getLectureId());
            lecture.setStatus(ContentStatus.DELETED);
            lectureRepository.save(lecture);
        }
    }
}