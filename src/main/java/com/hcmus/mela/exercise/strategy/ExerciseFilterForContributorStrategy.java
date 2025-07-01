package com.hcmus.mela.exercise.strategy;

import com.hcmus.mela.exercise.dto.dto.ExerciseDetailDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.request.UpdateExerciseRequest;
import com.hcmus.mela.exercise.exception.ExerciseException;
import com.hcmus.mela.exercise.mapper.ExerciseMapper;
import com.hcmus.mela.exercise.mapper.QuestionMapper;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.lecture.service.LectureService;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("EXERCISE_CONTRIBUTOR")
@RequiredArgsConstructor
public class ExerciseFilterForContributorStrategy implements ExerciseFilterStrategy {

    private final ExerciseRepository exerciseRepository;

    private final LectureService lectureService;

    @Override
    public List<ExerciseDetailDto> getExercises(UUID userId) {
        List<Exercise> verifiedExercises = exerciseRepository.findAllByStatus(ContentStatus.VERIFIED);
        List<Exercise> pendingExercises = exerciseRepository.findAllByStatusAndCreatedBy(ContentStatus.PENDING, userId);
        List<Exercise> deniedExercises = exerciseRepository.findAllByStatusAndCreatedBy(ContentStatus.DENIED, userId);
        // Combine all exercises
        verifiedExercises.addAll(pendingExercises);
        verifiedExercises.addAll(deniedExercises);
        if (verifiedExercises.isEmpty()) {
            return List.of();
        }
        return verifiedExercises.stream()
                .map(exercise -> {
                    ExerciseDetailDto exerciseDetailDto = ExerciseMapper.INSTANCE.exerciseToExerciseDetailDto(exercise);
                    exerciseDetailDto.setTotalQuestions(exercise.getQuestions().size());
                    return exerciseDetailDto;
                })
                .toList();
    }

    @Override
    public ExerciseDto createExercise(UUID userId, Exercise exercise) {
        if (exercise.getLectureId() == null || lectureService.isLectureAssignableToExercise(exercise.getLectureId(), userId)) {
            throw new ExerciseException("Lecture is not assignable to this exercise");
        }
        Exercise savedExercise = exerciseRepository.save(exercise);
        return ExerciseMapper.INSTANCE.converToExerciseDto(savedExercise);
    }

    @Override
    public ExerciseDto getExerciseById(UUID userId, UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found"));
        if (exercise.getStatus() == ContentStatus.DELETED) {
            throw new ExerciseException("Exercise has been deleted");
        }
        if (exercise.getCreatedBy().equals(userId) || exercise.getStatus() == ContentStatus.VERIFIED) {
            return ExerciseMapper.INSTANCE.converToExerciseDto(exercise);
        }
        throw new ExerciseException("Contributor cannot view this exercise");
    }

    @Override
    public void updateExercise(UUID userId, UUID exerciseId, UpdateExerciseRequest updateRequest) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found"));
        if (exercise.getStatus() == ContentStatus.DELETED || exercise.getStatus() == ContentStatus.VERIFIED) {
            throw new ExerciseException("Contributor cannot update a deleted or verified exercise");
        }
        if (!lectureService.isLectureAssignableToExercise(updateRequest.getLectureId(), userId)) {
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
        exercise.setStatus(ContentStatus.PENDING);
        exercise.setRejectedReason(null);
        exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExercise(UUID userId, UUID exerciseId) {
        Exercise exercise = exerciseRepository.findByExerciseIdAndCreatedBy(exerciseId, userId)
                .orElseThrow(() -> new ExerciseException("Contributor exercise not found"));
        if (exercise.getStatus() == ContentStatus.VERIFIED) {
            throw new ExerciseException("Cannot delete a verified exercise");
        }
        exercise.setStatus(ContentStatus.DELETED);
        exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExercisesByLecture(UUID userId, UUID lectureId) {
        List<Exercise> exercises = exerciseRepository.findAllByLectureIdAndCreatedBy(lectureId, userId);
        if (exercises.isEmpty()) {
            return;
        }
        for (Exercise exercise : exercises) {
            exercise.setStatus(ContentStatus.DELETED);
            exerciseRepository.save(exercise);
        }
    }
}