package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.lecture.dto.dto.LevelDto;
import com.hcmus.mela.lecture.dto.request.UpdateLevelRequest;

import java.util.List;
import java.util.UUID;

public interface LevelFilterStrategy {
    List<LevelDto> getLevels(UUID userId);

    void updateLevel(UUID userId, UUID levelId, UpdateLevelRequest updateLevelRequest);
}