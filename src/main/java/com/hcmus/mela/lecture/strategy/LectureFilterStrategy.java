package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.lecture.dto.dto.LectureDto;

import java.util.List;
import java.util.UUID;

public interface LectureFilterStrategy {
    List<LectureDto> getLectures(UUID userId);
}