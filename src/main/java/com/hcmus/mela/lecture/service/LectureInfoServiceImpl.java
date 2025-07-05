package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.mapper.LectureMapper;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.repository.LectureRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LectureInfoServiceImpl implements LectureInfoService {

    private final LectureRepository lectureRepository;

    @Override
    public LectureDto findLectureByLectureId(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElse(null);
        return lecture == null ? null : LectureMapper.INSTANCE.lectureToLectureDto(lecture);
    }

    @Override
    public LectureDto findLectureByLectureIdAndStatus(UUID lectureId, ContentStatus status) {
        Lecture lecture = lectureRepository.findByLectureIdAndStatus(lectureId, status).orElse(null);
        return lecture == null ? null : LectureMapper.INSTANCE.lectureToLectureDto(lecture);
    }

    @Override
    public LectureDto findLectureByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber) {
        Lecture lecture = lectureRepository.findByTopicIdAndLevelIdAndOrdinalNumber(topicId, levelId, ordinalNumber);
        if (lecture == null || lecture.getStatus() != ContentStatus.VERIFIED) {
            return null;
        }
        return LectureMapper.INSTANCE.lectureToLectureDto(lecture);
    }

    @Override
    public Integer findLectureOrdinalNumberByLectureId(UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureId(lectureId);
        return lecture == null ? -1 : lecture.getOrdinalNumber();
    }

    @Override
    public List<LectureDto> findLecturesNeedToBeReviewed(UUID userId) {
        List<Lecture> lectures = lectureRepository.findCompleteLecturesWithWrongExercises(userId);
        return lectures.stream()
                .map(LectureMapper.INSTANCE::lectureToLectureDto)
                .toList();
    }

    @Override
    public List<LectureDto> findLecturesNeedToBeSuggested(UUID userId) {
        return List.of();
    }
}
