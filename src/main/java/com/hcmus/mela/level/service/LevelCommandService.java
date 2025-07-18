package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.request.CreateLevelRequest;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.dto.response.CreateLevelResponse;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;

import java.util.UUID;

public interface LevelCommandService {

    CreateLevelResponse createLevel(LevelFilterStrategy strategy, UUID userId, CreateLevelRequest request);

    void updateLevel(LevelFilterStrategy strategy, UUID userId, UUID levelId, UpdateLevelRequest request);

    void deleteLevel(LevelFilterStrategy strategy, UUID userId, UUID levelId);
}
