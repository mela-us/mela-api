package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.shared.type.ContentStatus;

import java.util.UUID;

public interface ExerciseInfoService {

    ExerciseDto findByExerciseId(UUID exerciseId);

    boolean checkExerciseStatus(UUID exerciseId, ContentStatus status);

    ExerciseDto findByExerciseIdAndStatus(UUID exerciseId, ContentStatus status);
}
