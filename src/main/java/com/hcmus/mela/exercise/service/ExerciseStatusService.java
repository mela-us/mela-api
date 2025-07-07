package com.hcmus.mela.exercise.service;

import com.hcmus.mela.shared.type.ContentStatus;

import java.util.UUID;

public interface ExerciseStatusService {

    void denyExercise(UUID exerciseId, String reason);

    void approveExercise(UUID exerciseId);

    boolean isExerciseInStatus(UUID exerciseId, ContentStatus status);
}
