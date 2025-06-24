package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.response.GetLecturesByLevelResponse;
import com.hcmus.mela.lecture.dto.response.GetLecturesWithStatsResponse;

import java.util.List;
import java.util.UUID;

public interface LectureListService {

    GetLecturesByLevelResponse getLecturesByLevel(UUID userId, UUID levelId);

    GetLecturesWithStatsResponse getLecturesByKeyword(UUID userId, String keyword);

    GetLecturesWithStatsResponse getLecturesByRecent(UUID userId, Integer size);

    List<LectureDto> getLecturesNeedToBeReviewed(UUID userId);

    List<LectureDto> getLecturesNeedToBeSuggested(UUID userId);
}