package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;

import java.util.List;
import java.util.UUID;

public interface LectureFilterStrategy {
    List<LectureDto> getLectures(UUID userId);

    void updateLecture(UUID userId, UUID lectureId, UpdateLectureRequest updateLectureRequest);
}