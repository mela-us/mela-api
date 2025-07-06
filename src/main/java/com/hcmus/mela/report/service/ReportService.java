package com.hcmus.mela.report.service;

import com.hcmus.mela.report.dto.response.*;

public interface ReportService {

    NewUsersStatResponse getNewUsersStat();

    CompletedTestStatResponse getCompletedTestsStat();

    CompletedExercisesStatResponse getCompletedExercisesStat();

    ExerciseAverageTimeStatResponse getExerciseAverageTimeStat();

    HourlyExerciseDataResponse getHourlyExerciseData();

    UserGrowthDataResponse getUserGrowthData();

    AverageTimeByLevelDataResponse getAverageTimeByLevelData();

    TopicLevelHeatmapDataResponse getTopicLevelHeatmapData();
}