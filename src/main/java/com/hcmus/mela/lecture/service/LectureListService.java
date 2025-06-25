package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.response.GetLecturesByLevelResponse;
import com.hcmus.mela.lecture.dto.response.GetLecturesResponse;
import com.hcmus.mela.lecture.dto.response.GetLecturesWithStatsResponse;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;
import com.hcmus.mela.level.dto.response.GetLevelsResponse;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;

import java.util.List;
import java.util.UUID;

public interface LectureListService {

    GetLecturesResponse getLecturesResponse(LectureFilterStrategy strategy, UUID userId);

    GetLecturesByLevelResponse getLecturesByLevel(UUID userId, UUID levelId);

    GetLecturesWithStatsResponse getLecturesByKeyword(UUID userId, String keyword);

    GetLecturesWithStatsResponse getLecturesByRecent(UUID userId, Integer size);

    List<LectureDto> getLecturesNeedToBeReviewed(UUID userId);

    List<LectureDto> getLecturesNeedToBeSuggested(UUID userId);
}