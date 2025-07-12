package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.response.GetAllExercisesResponse;
import com.hcmus.mela.exercise.dto.response.GetExerciseInfoResponse;
import com.hcmus.mela.exercise.dto.response.GetExercisesInLectureResponse;
import com.hcmus.mela.exercise.strategy.ExerciseFilterStrategy;

import java.util.UUID;

public interface ExerciseQueryService {

    GetExercisesInLectureResponse getExercisesByLectureId(UUID lectureId, UUID userId);

    GetAllExercisesResponse getAllExercises(ExerciseFilterStrategy strategy, UUID userId);

    GetExerciseInfoResponse getExerciseInfoByExerciseId(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId);
}
