package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.request.CreateLevelRequest;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.dto.response.CreateLevelResponse;
import com.hcmus.mela.level.dto.response.GetLevelsResponse;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;

import java.util.UUID;

public interface LevelService {

    GetLevelsResponse getLevelsResponse(LevelFilterStrategy levelFilterStrategy, UUID userId);

    CreateLevelResponse getCreateLevelResponse(UUID creatorId, CreateLevelRequest createLevelRequest);

    void updateLevel(LevelFilterStrategy strategy, UUID userId, UUID levelId, UpdateLevelRequest request);

    void denyLevel(UUID levelId, String reason);

    void approveLevel(UUID levelId);

    Level findLevelByLevelId(UUID id);

    Level findLevelByLevelTitle(String title);
}
