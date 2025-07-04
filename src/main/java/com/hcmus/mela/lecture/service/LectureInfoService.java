package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.shared.type.ContentStatus;

import java.util.List;
import java.util.UUID;

public interface LectureInfoService {

    LectureDto findLectureByLectureId(UUID lectureId);

    LectureDto findLectureByLectureIdAndStatus(UUID lectureId, ContentStatus status);

    LectureDto findLectureByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber);

    Integer findLectureOrdinalNumberByLectureId(UUID lectureId);

    List<LectureDto> findLecturesNeedToBeReviewed(UUID userId);

    List<LectureDto> findLecturesNeedToBeSuggested(UUID userId);
}
