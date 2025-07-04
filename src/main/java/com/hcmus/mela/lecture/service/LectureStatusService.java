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

public interface LectureStatusService {

    void denyLecture(UUID lectureId, String reason);

    void approveLecture(UUID lectureId);

    boolean isLectureInStatus(UUID lectureId, ContentStatus status);

    boolean isLectureAssignableToExercise(UUID userId, UUID lectureId);
}