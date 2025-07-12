package com.hcmus.mela.history.service;

import com.hcmus.mela.history.dto.dto.ExerciseHistoryDto;
import com.hcmus.mela.history.dto.request.ExerciseResultRequest;
import com.hcmus.mela.history.dto.response.ExerciseResultResponse;
import com.hcmus.mela.history.dto.response.GetUserExerciseStatsResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExerciseHistoryService {

    ExerciseResultResponse getExerciseResultResponse(UUID userId, ExerciseResultRequest request);

    Map<UUID, Integer> getPassedExerciseCountOfUser(UUID userId);

    Map<UUID, Double> getExerciseBestScoresOfUserByLecture(UUID userId, UUID lectureId);

    List<ExerciseHistoryDto> getExerciseHistoryByUserAndLevel(UUID userId, UUID levelId);

    void deleteAllExerciseHistoryByUserId(UUID userId);

    Integer countDoneExercisesByLectureId(UUID lectureId);

    Integer countDoneExerciseByExerciseId(UUID exerciseId);

    GetUserExerciseStatsResponse getUserExerciseStats(UUID userId);
}