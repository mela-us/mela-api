package com.hcmus.mela.level.strategy;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;

import java.util.List;
import java.util.UUID;

public interface LevelFilterStrategy {
    List<LevelDto> getLevels(UUID userId);

    void updateLevel(UUID userId, UUID levelId, UpdateLevelRequest updateLevelRequest);

    void deleteLevel(UUID userId, UUID levelId);
}