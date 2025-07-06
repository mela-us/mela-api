package com.hcmus.mela.report.controller;

import com.hcmus.mela.report.dto.response.*;
import com.hcmus.mela.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/new-users-stat")
    @Operation(tags = "📊 Report Service", summary = "Get new users statistics",
            description = "Retrieves statistics about new users, including current and previous counts and percentage change.")
    public ResponseEntity<NewUsersStatResponse> getNewUsersStat() {
        log.info("Fetching new users statistics");
        NewUsersStatResponse response = reportService.getNewUsersStat();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/completed-tests-stat")
    @Operation(tags = "📊 Report Service", summary = "Get completed test statistics",
            description = "Retrieves statistics about completed tests, including current and previous counts and percentage change.")
    public ResponseEntity<CompletedTestStatResponse> getTestStat() {
        log.info("Fetching test statistics");
        CompletedTestStatResponse response = reportService.getCompletedTestsStat();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/completed-exercises-stat")
    @Operation(tags = "📊 Report Service", summary = "Get completed exercises statistics",
            description = "Retrieves statistics about completed exercises, including current and previous counts and percentage change.")
    public ResponseEntity<CompletedExercisesStatResponse> getCompletedExercisesStat() {
        log.info("Fetching completed exercises statistics");
        CompletedExercisesStatResponse response = reportService.getCompletedExercisesStat();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/exercise-average-time-stat")
    @Operation(tags = "📊 Report Service", summary = "Get exercise average time statistics",
            description = "Retrieves statistics about average time to complete exercises, including current and previous times and percentage change.")
    public ResponseEntity<ExerciseAverageTimeStatResponse> getExerciseAverageTimeStat() {
        log.info("Fetching exercise average time statistics");
        ExerciseAverageTimeStatResponse response = reportService.getExerciseAverageTimeStat();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/hourly-exercise-data")
    @Operation(tags = "📊 Report Service", summary = "Get hourly exercise data",
            description = "Retrieves data about user exercise by hour of the day.")
    public ResponseEntity<HourlyExerciseDataResponse> getHourlyActivityData() {
        log.info("Fetching hourly exercise data");
        HourlyExerciseDataResponse response = reportService.getHourlyExerciseData();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/user-growth-data")
    @Operation(tags = "📊 Report Service", summary = "Get user growth data",
            description = "Retrieves data about user growth over months.")
    public ResponseEntity<UserGrowthDataResponse> getUserGrowthData() {
        log.info("Fetching user growth data");
        UserGrowthDataResponse response = reportService.getUserGrowthData();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/average-time-by-level-data")
    @Operation(tags = "📊 Report Service", summary = "Get average time by level data",
            description = "Retrieves data about average time to complete exercises by level.")
    public ResponseEntity<AverageTimeByLevelDataResponse> getAverageTimeByLevelData() {
        log.info("Fetching average time by level data");
        AverageTimeByLevelDataResponse response = reportService.getAverageTimeByLevelData();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/topic-level-heatmap-data")
    @Operation(tags = "📊 Report Service", summary = "Get topic-level heatmap data",
            description = "Retrieves data for a heatmap of topics across different levels.")
    public ResponseEntity<TopicLevelHeatmapDataResponse> getTopicLevelHeatmapData() {
        log.info("Fetching topic-level heatmap data");
        TopicLevelHeatmapDataResponse response = reportService.getTopicLevelHeatmapData();
        return ResponseEntity.ok(response);
    }
}