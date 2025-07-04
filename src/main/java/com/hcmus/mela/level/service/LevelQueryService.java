package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.request.CreateLevelRequest;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.dto.response.CreateLevelResponse;
import com.hcmus.mela.level.dto.response.GetLevelsResponse;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;
import com.hcmus.mela.shared.type.ContentStatus;

import java.util.UUID;

public interface LevelQueryService {

    GetLevelsResponse getLevelsResponse(LevelFilterStrategy levelFilterStrategy, UUID userId);

    boolean checkLevelStatus(UUID levelId, ContentStatus status);

    Level findLevelByLevelId(UUID id);

    Level findLevelByLevelTitle(String title);
}
