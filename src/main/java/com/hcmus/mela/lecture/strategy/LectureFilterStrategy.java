package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.lecture.model.Lecture;

import java.util.List;
import java.util.UUID;

public interface LectureFilterStrategy {

    List<LectureDto> getLectures(UUID userId);

    void updateLecture(UUID userId, UUID lectureId, UpdateLectureRequest request);

    LectureDto createLecture(UUID userId, Lecture lecture);

    LectureDto getLectureById(UUID userId, UUID lectureId);

    void deleteLecture(UUID userId, UUID lectureId);

    void deleteLecturesByTopic(UUID userId, UUID topicId);

    void deleteLecturesByLevel(UUID userId, UUID levelId);
}