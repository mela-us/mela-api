package com.hcmus.mela.exercise.strategy;

import com.hcmus.mela.exercise.dto.dto.ExerciseDetailDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.request.UpdateExerciseRequest;
import com.hcmus.mela.exercise.exception.ExerciseException;
import com.hcmus.mela.exercise.mapper.ExerciseMapper;
import com.hcmus.mela.exercise.mapper.QuestionMapper;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.lecture.service.LectureInfoService;
import com.hcmus.mela.lecture.service.LectureStatusService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("EXERCISE_CONTRIBUTOR")
@RequiredArgsConstructor
public class ExerciseFilterForContributorStrategy implements ExerciseFilterStrategy {

    private final ExerciseRepository exerciseRepository;
    private final LectureStatusService lectureStatusService;
    private final LectureInfoService lectureInfoService;
    private final UserInfoService userInfoService;

    @Override
    public List<ExerciseDetailDto> getExercises(UUID userId) {
        User user = userInfoService.getUserByUserId(userId);
        UUID levelId = user.getLevelId();
        List<Exercise> verifiedExercises = exerciseRepository.findAllByStatus(ContentStatus.VERIFIED);
        List<Exercise> pendingExercises = exerciseRepository.findAllByStatusAndCreatedBy(ContentStatus.PENDING, userId);
        List<Exercise> deniedExercises = exerciseRepository.findAllByStatusAndCreatedBy(ContentStatus.DENIED, userId);
        verifiedExercises.addAll(pendingExercises);
        verifiedExercises.addAll(deniedExercises);
        if (verifiedExercises.isEmpty()) {
            return List.of();
        }
        if (levelId != null) {
            List<UUID> lectureIdsByLevel = lectureInfoService.getLectureIdsByLevelId(levelId);
            verifiedExercises.removeIf(exercise -> !lectureIdsByLevel.contains(exercise.getLectureId()));
        }
        return verifiedExercises.stream()
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
    public ExerciseDto createExercise(UUID userId, Exercise exercise) {
        User user = userInfoService.getUserByUserId(userId);
        UUID levelId = user.getLevelId();
        if (levelId != null && !levelId.equals(lectureInfoService.getLevelIdOfLecture(exercise.getLectureId()))) {
            throw new ExerciseException("Exercise does not belong to the contributor's level");
        }
        if (!lectureStatusService.isLectureAssignableToExercise(userId, exercise.getLectureId())) {
            throw new ExerciseException("Lecture must be verified or belong to the contributor");
        }
        exercise.setExerciseId(UUID.randomUUID());
        exercise.setStatus(ContentStatus.PENDING);
        exercise.setCreatedBy(userId);
        Exercise savedExercise = exerciseRepository.save(exercise);
        ExerciseDto exerciseDto = ExerciseMapper.INSTANCE.exerciseToExerciseDto(savedExercise);
        if (exercise.getCreatedBy() != null) {
            exerciseDto.setCreator(userInfoService.getUserPreviewDtoByUserId(exercise.getCreatedBy()));
        }
        return exerciseDto;
    }

    @Override
    public ExerciseDto getExerciseById(UUID userId, UUID exerciseId) {
        User user = userInfoService.getUserByUserId(userId);
        UUID levelId = user.getLevelId();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found in the system"));
        if (exercise.getStatus() == ContentStatus.DELETED) {
            throw new ExerciseException("Exercise is deleted and cannot be retrieved");
        }
        if (levelId != null && !levelId.equals(lectureInfoService.getLevelIdOfLecture(exercise.getLectureId()))) {
            throw new ExerciseException("Exercise does not belong to the contributor's level");
        }
        if (exercise.getStatus() == ContentStatus.VERIFIED || userId.equals(exercise.getCreatedBy())) {
            ExerciseDto exerciseDto = ExerciseMapper.INSTANCE.exerciseToExerciseDto(exercise);
            if (exercise.getCreatedBy() != null) {
                exerciseDto.setCreator(userInfoService.getUserPreviewDtoByUserId(exercise.getCreatedBy()));
            }
            return exerciseDto;
        }
        throw new ExerciseException("Exercise is not verified or does not belong to the contributor");
    }

    @Override
    public void updateExercise(UUID userId, UUID exerciseId, UpdateExerciseRequest request) {
        Exercise exercise = exerciseRepository.findByExerciseIdAndCreatedBy(exerciseId, userId)
                .orElseThrow(() -> new ExerciseException("Exercise of the contributor not found"));
        if (exercise.getStatus() == ContentStatus.DELETED || exercise.getStatus() == ContentStatus.VERIFIED) {
            throw new ExerciseException("Contributor cannot update a verified or deleted exercise");
        }
        User user = userInfoService.getUserByUserId(userId);
        UUID levelId = user.getLevelId();
        if (levelId != null && !levelId.equals(lectureInfoService.getLevelIdOfLecture(request.getLectureId()))) {
            throw new ExerciseException("Contributor cannot update exercise with a different level");
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
        if (lectureStatusService.isLectureAssignableToExercise(userId, request.getLectureId())) {
            exercise.setLectureId(request.getLectureId());
        } else {
            throw new ExerciseException("Lecture must be verified or belong to the contributor");
        }
        exercise.setStatus(ContentStatus.PENDING);
        exercise.setRejectedReason(null);
        exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExercise(UUID userId, UUID exerciseId) {
        Exercise exercise = exerciseRepository.findByExerciseIdAndCreatedBy(exerciseId, userId)
                .orElseThrow(() -> new ExerciseException("Exercise of the contributor not found"));
        if (exercise.getStatus() == ContentStatus.VERIFIED) {
            throw new ExerciseException("Contributor cannot delete a verified exercise");
        }
        if (exercise.getStatus() == ContentStatus.DELETED) {
            return;
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