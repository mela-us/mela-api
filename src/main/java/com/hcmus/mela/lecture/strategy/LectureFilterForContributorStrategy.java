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
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("LECTURE_CONTRIBUTOR")
@RequiredArgsConstructor
public class LectureFilterForContributorStrategy implements LectureFilterStrategy {

    private final LectureRepository lectureRepository;
    private final LevelStatusService levelStatusService;
    private final TopicStatusService topicStatusService;
    private final UserInfoService userInfoService;
    private final ExerciseFilterForContributorStrategy exerciseFilterForContributorStrategy;

    @Override
    public List<LectureDto> getLectures(UUID userId) {
        User user = userInfoService.getUserByUserId(userId);
        List<Lecture> verifiedLectures = new ArrayList<>();
        if (user.getLevelId() == null) {
            verifiedLectures.addAll(lectureRepository.findAllByStatus(ContentStatus.VERIFIED));
            verifiedLectures.addAll(lectureRepository.findAllByStatusAndCreatedBy(ContentStatus.PENDING, userId));
            verifiedLectures.addAll(lectureRepository.findAllByStatusAndCreatedBy(ContentStatus.DENIED, userId));
        } else {
            verifiedLectures.addAll(lectureRepository.findAllByStatusAndLevelId(ContentStatus.VERIFIED, user.getLevelId()));
            verifiedLectures.addAll(lectureRepository.findAllByStatusAndCreatedByAndLevelId(ContentStatus.PENDING, userId, user.getLevelId()));
            verifiedLectures.addAll(lectureRepository.findAllByStatusAndCreatedByAndLevelId(ContentStatus.DENIED, userId, user.getLevelId()));
        }
        if (verifiedLectures.isEmpty()) {
            return List.of();
        }
        return verifiedLectures.stream()
                .map(lecture -> {
                    LectureDto lectureDto = LectureMapper.INSTANCE.lectureToLectureDto(lecture);
                    if (lecture.getCreatedBy() != null) {
                        lectureDto.setCreator(userInfoService.getUserPreviewDtoByUserId(lecture.getCreatedBy()));
                    }
                    return lectureDto;
                })
                .toList();
    }

    @Override
    public LectureDto createLecture(UUID userId, Lecture lecture) {
        User user = userInfoService.getUserByUserId(userId);
        UUID levelId = user.getLevelId();
        if (levelId != null && !levelId.equals(lecture.getLevelId())) {
            throw new LectureException("Lecture does not belong to the contributor's level");
        }
        if (!topicStatusService.isTopicAssignableToLecture(userId, lecture.getTopicId())) {
            throw new LectureException("Topic must be verified or belong to the contributor ");
        }
        if (!levelStatusService.isLevelAssignableToLecture(userId, lecture.getLevelId())) {
            throw new LectureException("Level must be verified or belong to the contributor ");
        }
        lecture.setLectureId(UUID.randomUUID());
        lecture.setStatus(ContentStatus.PENDING);
        lecture.setCreatedBy(userId);
        Lecture savedLecture = lectureRepository.save(lecture);
        LectureDto lectureDto = LectureMapper.INSTANCE.lectureToLectureDto(savedLecture);
        if (lecture.getCreatedBy() != null) {
            lectureDto.setCreator(userInfoService.getUserPreviewDtoByUserId(lecture.getCreatedBy()));
        }
        return lectureDto;
    }

    @Override
    public void updateLecture(UUID userId, UUID lectureId, UpdateLectureRequest request) {
        Lecture lecture = lectureRepository.findByLectureIdAndCreatedBy(lectureId, userId)
                .orElseThrow(() -> new LectureException("Lecture of the contributor not found"));
        if (lecture.getStatus() == ContentStatus.DELETED || lecture.getStatus() == ContentStatus.VERIFIED) {
            throw new LectureException("Contributor cannot update a deleted or verified lecture");
        }
        User user = userInfoService.getUserByUserId(userId);
        UUID levelId = user.getLevelId();
        if (levelId != null && !levelId.equals(request.getLevelId())) {
            throw new LectureException("Contributor cannot update a lecture with a different level");
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
        if (topicStatusService.isTopicAssignableToLecture(userId, request.getTopicId())) {
            lecture.setTopicId(request.getTopicId());
        } else {
            throw new LectureException("Topic must be verified or belong to the contributor");
        }
        if (levelStatusService.isLevelAssignableToLecture(userId, request.getLevelId())) {
            lecture.setLevelId(request.getLevelId());
        } else {
            throw new LectureException("Level must be verified or belong to the contributor");
        }
        lecture.setStatus(ContentStatus.PENDING);
        lecture.setRejectedReason(null);
        lectureRepository.save(lecture);
    }

    @Override
    public LectureDto getLectureById(UUID userId, UUID lectureId) {
        User user = userInfoService.getUserByUserId(userId);
        UUID levelId = user.getLevelId();
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found in the system"));
        if (lecture.getStatus() == ContentStatus.DELETED) {
            throw new LectureException("Lecture is deleted and cannot be retrieved");
        }
        if (levelId != null && !levelId.equals(lecture.getLevelId())) {
            throw new LectureException("Lecture does not belong to the contributor's level");
        }
        if (lecture.getStatus() == ContentStatus.VERIFIED || userId.equals(lecture.getCreatedBy())) {
            LectureDto lectureDto = LectureMapper.INSTANCE.lectureToLectureDto(lecture);
            if (lecture.getCreatedBy() != null) {
                lectureDto.setCreator(userInfoService.getUserPreviewDtoByUserId(lecture.getCreatedBy()));
            }
            return lectureDto;
        }
        throw new LectureException("Lecture is not verified or does not belong to the contributor");
    }

    @Transactional
    @Override
    public void deleteLecture(UUID userId, UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureIdAndCreatedBy(lectureId, userId)
                .orElseThrow(() -> new LectureException("Lecture of the contributor not found"));
        if (lecture.getStatus() == ContentStatus.VERIFIED) {
            throw new LectureException("Contributor cannot delete a verified lecture");
        }
        if (lecture.getStatus() == ContentStatus.DELETED) {
            return;
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
}