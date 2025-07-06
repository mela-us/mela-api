package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.request.CreateLectureRequest;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.lecture.dto.response.CreateLectureResponse;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;

import java.util.UUID;

public interface LectureCommandService {

    CreateLectureResponse createLecture(LectureFilterStrategy strategy, UUID userId, CreateLectureRequest request);

    void updateLecture(LectureFilterStrategy strategy, UUID userId, UUID lectureId, UpdateLectureRequest request);

    void deleteLecture(LectureFilterStrategy strategy, UUID userId, UUID lectureId);
}
