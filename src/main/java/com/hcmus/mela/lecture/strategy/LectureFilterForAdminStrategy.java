package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.exercise.strategy.ExerciseFilterForAdminStrategy;
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

@Component("LECTURE_ADMIN")
@RequiredArgsConstructor
public class LectureFilterForAdminStrategy implements LectureFilterStrategy {

    private final LectureRepository lectureRepository;

    private final TopicService topicService;

    private final LevelService levelService;

    private final ExerciseFilterForAdminStrategy exerciseFilterForAdminStrategy;

    @Override
    public List<LectureDto> getLectures(UUID userId) {
        List<Lecture> lectures = lectureRepository.findAll();
        if (lectures.isEmpty()) {
            return List.of();
        }
        return lectures.stream()
                .map(LectureMapper.INSTANCE::lectureToLectureDto)
                .toList();
    }

    @Override
    public LectureDto createLecture(UUID userId, Lecture lecture) {
        if (lecture.getTopicId() == null || topicService.checkTopicStatus(lecture.getTopicId(), ContentStatus.DELETED)) {
            throw new LectureException("Topic is not assignable to this lecture");
        }
        if (lecture.getLevelId() == null || levelService.checkLevelStatus(lecture.getLevelId(), ContentStatus.DELETED)) {
            throw new LectureException("Level is not assignable to this lecture");
        }
        Lecture savedLecture = lectureRepository.save(lecture);
        return LectureMapper.INSTANCE.lectureToLectureDto(savedLecture);
    }

    @Override
    public LectureDto getLectureById(UUID userId, UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found"));
        return LectureMapper.INSTANCE.lectureToLectureDto(lecture);
    }

    @Override
    public void deleteLecture(UUID userId, UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found"));
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

    @Override
    public void updateLecture(UUID userId, UUID lectureId, UpdateLectureRequest updateLectureRequest) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found"));
        if (lecture.getStatus() == ContentStatus.DELETED) {
            throw new LectureException("Cannot update a deleted lecture");
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
        if (!topicService.checkTopicStatus(updateLectureRequest.getTopicId(), ContentStatus.DELETED)) {
            lecture.setTopicId(updateLectureRequest.getTopicId());
        } else {
            throw new LectureException("Topic is not assignable to this lecture");
        }
        if (!levelService.checkLevelStatus(updateLectureRequest.getLevelId(), ContentStatus.DELETED)) {
            lecture.setLevelId(updateLectureRequest.getLevelId());
        } else {
            throw new LectureException("Level is not assignable to this lecture");
        }
        lectureRepository.save(lecture);
    }
}