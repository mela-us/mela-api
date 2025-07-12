package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.response.*;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;

import java.util.UUID;

public interface LectureQueryService {

    GetAllLecturesResponse getAllLectures(LectureFilterStrategy strategy, UUID userId);

    GetLectureInfoResponse getLectureInfoByLectureId(LectureFilterStrategy strategy, UUID userId, UUID lectureId);

    GetLecturesByLevelResponse getLecturesByLevelId(UUID userId, UUID levelId);

    GetLecturesWithStatsResponse getLecturesByKeyword(UUID userId, String keyword);

    GetLectureSectionsResponse getLectureSectionsByLectureId(UUID lectureId);

    GetLectureContributionResponse getLectureContribution(UUID userId);
}