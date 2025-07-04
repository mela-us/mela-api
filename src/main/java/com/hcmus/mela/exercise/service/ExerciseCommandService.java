package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.request.CreateExerciseRequest;
import com.hcmus.mela.exercise.dto.request.UpdateExerciseRequest;
import com.hcmus.mela.exercise.dto.response.CreateExerciseResponse;
import com.hcmus.mela.exercise.dto.response.GetExerciseInfoResponse;
import com.hcmus.mela.exercise.strategy.ExerciseFilterStrategy;
import com.hcmus.mela.shared.type.ContentStatus;

import java.util.UUID;

public interface ExerciseCommandService {

    CreateExerciseResponse createExercise(ExerciseFilterStrategy strategy, UUID userId, CreateExerciseRequest request);

    void updateExercise(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId, UpdateExerciseRequest request);

    void deleteExercise(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId);
}
