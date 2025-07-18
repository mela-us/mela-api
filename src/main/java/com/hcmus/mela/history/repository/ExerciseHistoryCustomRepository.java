package com.hcmus.mela.history.repository;

import com.hcmus.mela.history.model.BestResultByExercise;
import com.hcmus.mela.history.model.ExercisesCountByLecture;

import java.util.List;
import java.util.UUID;

public interface ExerciseHistoryCustomRepository {

    List<ExercisesCountByLecture> countTotalPassExerciseOfUser(UUID userId, Double passScore);

    List<BestResultByExercise> getBestExerciseResultsOfUserByLectureId(UUID userId, UUID lectureId);
}
