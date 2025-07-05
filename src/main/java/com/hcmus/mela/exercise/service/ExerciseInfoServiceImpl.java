package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.mapper.ExerciseMapper;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseInfoServiceImpl implements ExerciseInfoService {

    private final ExerciseRepository exerciseRepository;

    @Override
    public ExerciseDto findExerciseByExerciseId(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElse(null);
        return exercise == null ? null : ExerciseMapper.INSTANCE.exerciseToExerciseDto(exercise);
    }

    @Override
    public ExerciseDto findExerciseByExerciseIdAndStatus(UUID exerciseId, ContentStatus status) {
        Exercise exercise = exerciseRepository.findByExerciseIdAndStatus(exerciseId, status).orElse(null);
        return exercise == null ? null : ExerciseMapper.INSTANCE.exerciseToExerciseDto(exercise);
    }

    @Override
    public List<ExerciseDto> findExercisesByLectureId(UUID lectureId) {
        List<Exercise> exercises = exerciseRepository.findAllByLectureId(lectureId);
        if (exercises.isEmpty()) {
            return List.of();
        }
        return exercises.stream()
                .map(ExerciseMapper.INSTANCE::exerciseToExerciseDto)
                .toList();
    }

    @Override
    public List<ExerciseDto> findExercisesByLectureIdAndStatus(UUID lectureId, ContentStatus status) {
        List<Exercise> exercises = exerciseRepository.findAllByLectureIdAndStatus(lectureId, status);
        if (exercises.isEmpty()) {
            return List.of();
        }
        return exercises.stream()
                .map(ExerciseMapper.INSTANCE::exerciseToExerciseDto)
                .toList();
    }
}
