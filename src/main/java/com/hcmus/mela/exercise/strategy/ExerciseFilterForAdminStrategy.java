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
import com.hcmus.mela.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("EXERCISE_ADMIN")
@RequiredArgsConstructor
public class ExerciseFilterForAdminStrategy implements ExerciseFilterStrategy {

    private final ExerciseRepository exerciseRepository;
    private final LectureStatusService lectureStatusService;
    private final UserInfoService userInfoService;

    @Override
    public List<ExerciseDetailDto> getExercises(UUID userId) {
        List<Exercise> exercises = exerciseRepository.findAll();
        if (exercises.isEmpty()) {
            return List.of();
        }
        return exercises.stream()
                .filter(exercise -> exercise.getStatus() != ContentStatus.DELETED)
                .map(exercise -> {
                    ExerciseDetailDto exerciseDetailDto = ExerciseMapper.INSTANCE.exerciseToExerciseDetailDto(exercise);
                    if (exercise.getQuestions() == null || exercise.getQuestions().isEmpty()) {
                        exerciseDetailDto.setTotalQuestions(0);
                    } else {
                        exerciseDetailDto.setTotalQuestions(exercise.getQuestions().size());
                    }
                    if (exercise.getCreatedBy() != null) {
                        exerciseDetailDto.setCreator(userInfoService.getUserPreviewDtoByUserId(exercise.getCreatedBy()));
                    }
                    return exerciseDetailDto;
                })
                .toList();
    }

    @Override
    public ExerciseDto getExerciseById(UUID userId, UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found in the system"));
        if (exercise.getStatus() == ContentStatus.DELETED) {
            throw new ExerciseException("Exercise is deleted and cannot be retrieved");
        }
        ExerciseDto exerciseDto = ExerciseMapper.INSTANCE.exerciseToExerciseDto(exercise);
        if (exercise.getCreatedBy() != null) {
            exerciseDto.setCreator(userInfoService.getUserPreviewDtoByUserId(exercise.getCreatedBy()));
        }
        return exerciseDto;
    }

    @Override
    public ExerciseDto createExercise(UUID userId, Exercise exercise) {
        if (lectureStatusService.isLectureInStatus(exercise.getLectureId(), ContentStatus.DELETED)) {
            throw new ExerciseException("Deleted lecture cannot be assigned to exercise");
        }
        exercise.setExerciseId(UUID.randomUUID());
        exercise.setStatus(ContentStatus.PENDING);
        Exercise savedExercise = exerciseRepository.save(exercise);
        return ExerciseMapper.INSTANCE.exerciseToExerciseDto(savedExercise);
    }

    @Override
    public void updateExercise(UUID userId, UUID exerciseId, UpdateExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found in the system"));
        if (exercise.getStatus() == ContentStatus.DELETED) {
            throw new ExerciseException("Cannot update a deleted exercise");
        }
        if (request.getExerciseName() != null && !request.getExerciseName().isEmpty()) {
            exercise.setExerciseName(request.getExerciseName());
        }
        if (request.getOrdinalNumber() != null && request.getOrdinalNumber() > 0) {
            exercise.setOrdinalNumber(request.getOrdinalNumber());
        }
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            exercise.setQuestions(request.getQuestions().stream().map(QuestionMapper.INSTANCE::updateQuestionRequestToQuestion).toList());
        }
        if (exercise.getStatus() == ContentStatus.VERIFIED
                && !lectureStatusService.isLectureInStatus(request.getLectureId(), ContentStatus.VERIFIED)) {
            throw new ExerciseException("Verified exercise must have a verified lecture");
        }
        if (!lectureStatusService.isLectureInStatus(request.getLectureId(), ContentStatus.DELETED)) {
            exercise.setLectureId(request.getLectureId());
        } else {
            throw new ExerciseException("Deleted lecture cannot be assigned to this exercise");
        }
        if (exercise.getStatus() == ContentStatus.DENIED) {
            exercise.setStatus(ContentStatus.PENDING);
            exercise.setRejectedReason(null);
        }
        exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExercise(UUID userId, UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found in the system"));
        if (exercise.getStatus() == ContentStatus.DELETED) {
            return;
        }
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