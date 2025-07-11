package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.exception.ExerciseException;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.lecture.service.LectureStatusService;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseStatusServiceImpl implements ExerciseStatusService {

    private final ExerciseRepository exerciseRepository;
    private final LectureStatusService lectureStatusService;

    @Override
    public void denyExercise(UUID exerciseId, String reason) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found in the system"));
        if (exercise.getStatus() == ContentStatus.VERIFIED || exercise.getStatus() == ContentStatus.DELETED) {
            throw new ExerciseException("Verified or deleted exercise cannot be denied");
        }
        exercise.setRejectedReason(reason);
        exercise.setStatus(ContentStatus.DENIED);
        exerciseRepository.save(exercise);
    }

    @Override
    public void approveExercise(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found in the system"));
        if (exercise.getStatus() == ContentStatus.DELETED) {
            throw new ExerciseException("Deleted exercise cannot be approved");
        }
        if (!lectureStatusService.isLectureInStatus(exercise.getLectureId(), ContentStatus.VERIFIED)) {
            throw new ExerciseException("Lecture of exercise must be verified before approving exercise");
        }
        exercise.setRejectedReason(null);
        exercise.setStatus(ContentStatus.VERIFIED);
        exerciseRepository.save(exercise);
    }

    @Override
    public boolean isExerciseInStatus(UUID exerciseId, ContentStatus status) {
        if (exerciseId == null || status == null) {
            return false;
        }
        Exercise exercise = exerciseRepository.findById(exerciseId).orElse(null);
        return exercise != null && exercise.getStatus() == status;
    }
}
