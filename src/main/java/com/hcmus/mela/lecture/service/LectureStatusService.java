package com.hcmus.mela.lecture.service;

import com.hcmus.mela.shared.type.ContentStatus;

import java.util.UUID;

public interface LectureStatusService {

    void denyLecture(UUID lectureId, String reason);

    void approveLecture(UUID lectureId);

    boolean isLectureInStatus(UUID lectureId, ContentStatus status);

    boolean isLectureAssignableToExercise(UUID userId, UUID lectureId);
}