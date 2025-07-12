package com.hcmus.mela.exercise.strategy;

import com.hcmus.mela.exercise.dto.dto.ExerciseDetailDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.request.UpdateExerciseRequest;
import com.hcmus.mela.exercise.exception.ExerciseException;
import com.hcmus.mela.exercise.mapper.ExerciseMapper;
import com.hcmus.mela.exercise.mapper.QuestionMapper;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.lecture.service.LectureStatusService;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component("EXERCISE_ADMIN")
@RequiredArgsConstructor
public class ExerciseFilterForAdminStrategy implements ExerciseFilterStrategy {

    private final ExerciseRepository exerciseRepository;
    private final LectureStatusService lectureStatusService;

    @Override
    public List<ExerciseDetailDto> getExercises(UUID userId) {
        List<Exercise> exercises = exerciseRepository.findAll();
        if (exercises.isEmpty()) {
            return List.of();
        }
        return exercises.stream()
                .map(exercise -> {
                    if (exercise.getStatus() == ContentStatus.DELETED) {
                        return null;
                    }
                    ExerciseDetailDto exerciseDetailDto = ExerciseMapper.INSTANCE.exerciseToExerciseDetailDto(exercise);
                    exerciseDetailDto.setTotalQuestions(exercise.getQuestions().size());
                    return exerciseDetailDto;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public ExerciseDto getExerciseById(UUID userId, UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found"));
        return ExerciseMapper.INSTANCE.exerciseToExerciseDto(exercise);
    }

    @Override
    public ExerciseDto createExercise(UUID userId, Exercise exercise) {
        if (exercise.getLectureId() == null || lectureStatusService.isLectureInStatus(exercise.getLectureId(), ContentStatus.DELETED)) {
            throw new ExerciseException("Lecture is not assignable to this exercise");
        }
        Exercise savedExercise = exerciseRepository.save(exercise);
        return ExerciseMapper.INSTANCE.exerciseToExerciseDto(savedExercise);
    }

    @Override
    public void updateExercise(UUID userId, UUID exerciseId, UpdateExerciseRequest updateRequest) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found"));
        if (exercise.getStatus() == ContentStatus.DELETED) {
            throw new ExerciseException("Cannot update a deleted exercise");
        }
        if (!lectureStatusService.isLectureInStatus(updateRequest.getLectureId(), ContentStatus.DELETED)) {
            exercise.setLectureId(updateRequest.getLectureId());
        } else {
            throw new ExerciseException("Lecture is not assignable to this exercise");
        }
        if (!updateRequest.getQuestions().isEmpty()) {
            exercise.setQuestions(updateRequest.getQuestions().stream().map(QuestionMapper.INSTANCE::updateQuestionRequestToQuestion).toList());
        }
        if (updateRequest.getExerciseName() != null && !updateRequest.getExerciseName().isEmpty()) {
            exercise.setExerciseName(updateRequest.getExerciseName());
        }
        if (updateRequest.getOrdinalNumber() != null && updateRequest.getOrdinalNumber() > 0) {
            exercise.setOrdinalNumber(updateRequest.getOrdinalNumber());
        }
        exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExercise(UUID userId, UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found"));
        exercise.setStatus(ContentStatus.DELETED);
        exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExercisesByLecture(UUID userId, UUID lectureId) {
        List<Exercise> exercises = exerciseRepository.findAllByLectureId(lectureId);
        if (exercises.isEmpty()) {
            return;
        }
        for (Exercise exercise : exercises) {
            exercise.setStatus(ContentStatus.DELETED);
            exerciseRepository.save(exercise);
        }
    }
}