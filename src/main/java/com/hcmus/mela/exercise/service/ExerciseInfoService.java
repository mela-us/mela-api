package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.shared.type.ContentStatus;

import java.util.List;
import java.util.UUID;

public interface ExerciseInfoService {

    ExerciseDto findExerciseByExerciseId(UUID exerciseId);

    ExerciseDto findExerciseByExerciseIdAndStatus(UUID exerciseId, ContentStatus status);

    List<ExerciseDto> findExercisesByLectureId(UUID lectureId);

    List<ExerciseDto> findExercisesByLectureIdAndStatus(UUID lectureId, ContentStatus status);

    void changeExerciseOwnerToAdmin(UUID previousUserId);
}
