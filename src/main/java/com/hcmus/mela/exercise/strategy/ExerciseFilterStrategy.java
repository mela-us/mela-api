package com.hcmus.mela.exercise.strategy;

import com.hcmus.mela.exercise.dto.dto.ExerciseDetailDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.request.UpdateExerciseRequest;
import com.hcmus.mela.exercise.model.Exercise;

import java.util.List;
import java.util.UUID;

public interface ExerciseFilterStrategy {

    List<ExerciseDetailDto> getExercises(UUID userId);

    ExerciseDto getExerciseById(UUID userId, UUID exerciseId);

    ExerciseDto createExercise(UUID userId, Exercise exercise);

    void updateExercise(UUID userId, UUID exerciseId, UpdateExerciseRequest updateRequest);

    void deleteExercise(UUID userId, UUID exerciseId);

    void deleteExercisesByLecture(UUID userId, UUID lectureId);
}