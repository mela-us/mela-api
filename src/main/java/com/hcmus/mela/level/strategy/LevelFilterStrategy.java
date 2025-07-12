package com.hcmus.mela.level.strategy;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.model.Level;

import java.util.List;
import java.util.UUID;

public interface LevelFilterStrategy {

    List<LevelDto> getLevels(UUID userId);

    LevelDto createLevel(UUID userId, Level level);

    void updateLevel(UUID userId, UUID levelId, UpdateLevelRequest request);

    void deleteLevel(UUID userId, UUID levelId);
}