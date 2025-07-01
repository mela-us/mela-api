package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.request.CreateExerciseRequest;
import com.hcmus.mela.exercise.dto.request.ExerciseRequest;
import com.hcmus.mela.exercise.dto.request.UpdateExerciseRequest;
import com.hcmus.mela.exercise.dto.response.*;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.strategy.ExerciseFilterStrategy;
import com.hcmus.mela.shared.type.ContentStatus;

import java.util.List;
import java.util.UUID;

public interface ExerciseService {

    ExerciseResponse getAllExercisesInLecture(ExerciseRequest exerciseRequest);

    GetExercisesResponse getExercisesResponse(ExerciseFilterStrategy strategy, UUID userId);

    CreateExerciseResponse getCreateExerciseResponse(ExerciseFilterStrategy strategy, UUID userId, CreateExerciseRequest request);

    void updateExercise(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId, UpdateExerciseRequest request);

    GetExerciseInfoResponse getExerciseInfoResponse(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId);

    void denyExercise(UUID exerciseId, String reason);

    void approveExercise(UUID exerciseId);

    boolean checkExerciseStatus(UUID exerciseId, ContentStatus status);

    void deleteExercise(ExerciseFilterStrategy strategy,UUID exerciseId, UUID userId);

    QuestionResponse getListQuestionsOfExercise(ExerciseRequest exerciseRequest);

    List<ExerciseDto> getListOfExercisesInLecture(UUID lectureId);

    Exercise findByQuestionId(UUID questionId);

    void updateQuestionHint(Exercise exercise);
}
