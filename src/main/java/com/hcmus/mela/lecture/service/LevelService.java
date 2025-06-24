package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.request.CreateLevelRequest;
import com.hcmus.mela.lecture.dto.request.UpdateLevelRequest;
import com.hcmus.mela.lecture.dto.response.CreateLevelResponse;
import com.hcmus.mela.lecture.dto.response.GetLevelsResponse;
import com.hcmus.mela.lecture.model.Level;
import com.hcmus.mela.lecture.strategy.LevelFilterStrategy;

import java.util.UUID;

public interface LevelService {

    GetLevelsResponse getLevelsResponse(LevelFilterStrategy levelFilterStrategy, UUID userId);

    CreateLevelResponse getCreateLevelResponse(UUID creatorId, CreateLevelRequest createLevelRequest);

    void updateLevel(LevelFilterStrategy strategy, UUID userId, UUID levelId, UpdateLevelRequest request);

    Level findLevelByLevelId(UUID id);

    Level findLevelByLevelTitle(String title);
}
