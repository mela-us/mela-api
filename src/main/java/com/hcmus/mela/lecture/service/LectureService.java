package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.request.CreateLectureRequest;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.lecture.dto.response.CreateLectureResponse;
import com.hcmus.mela.lecture.dto.response.GetLectureInfoResponse;
import com.hcmus.mela.lecture.dto.response.GetLectureSectionsResponse;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;
import com.hcmus.mela.shared.type.ContentStatus;

import java.util.UUID;

public interface LectureService {

    CreateLectureResponse getCreateLectureResponse(LectureFilterStrategy strategy, UUID userId, CreateLectureRequest request);

    void updateLecture(LectureFilterStrategy strategy, UUID userId, UUID lectureId, UpdateLectureRequest request);

    void deleteLecture(LectureFilterStrategy strategy, UUID lectureId, UUID userId);

    void denyLecture(UUID lectureId, String reason);

    void approveLecture(UUID lectureId);

    boolean isLectureAssignableToExercise(UUID lectureId, UUID userId);

    boolean checkLectureStatus(UUID lectureId, ContentStatus status);

    GetLectureInfoResponse getLectureInfoResponse(LectureFilterStrategy strategy, UUID userId, UUID lectureId);

    LectureDto getLectureById(UUID lectureId);

    LectureDto getLectureByIdAndStatus(UUID lectureId, ContentStatus status);

    Integer getLectureOrdinalNumber(UUID lectureId);

    GetLectureSectionsResponse getLectureSections(UUID lectureId);

    LectureDto getLectureByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber);
}