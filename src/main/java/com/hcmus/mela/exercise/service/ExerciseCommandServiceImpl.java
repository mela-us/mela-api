package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.request.CreateExerciseRequest;
import com.hcmus.mela.exercise.dto.request.UpdateExerciseRequest;
import com.hcmus.mela.exercise.dto.response.CreateExerciseResponse;
import com.hcmus.mela.exercise.mapper.ExerciseMapper;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.strategy.ExerciseFilterStrategy;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseCommandServiceImpl implements ExerciseCommandService {

    @Override
    public CreateExerciseResponse createExercise(ExerciseFilterStrategy strategy, UUID userId, CreateExerciseRequest request) {
        Exercise exercise = ExerciseMapper.INSTANCE.createExerciseRequestToExercise(request);
        exercise.setExerciseId(UUID.randomUUID());
        exercise.setStatus(ContentStatus.PENDING);
        exercise.setCreatedBy(userId);
        ExerciseDto exerciseDto = strategy.createExercise(userId, exercise);
        return new CreateExerciseResponse("Create exercise successfully", exerciseDto);
    }

    @Override
    public void updateExercise(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId, UpdateExerciseRequest request) {
        strategy.updateExercise(userId, exerciseId, request);
    }

    @Override
    public void deleteExercise(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId) {
        strategy.deleteExercise(userId, exerciseId);
    }
}
