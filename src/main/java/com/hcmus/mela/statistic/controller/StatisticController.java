package com.hcmus.mela.statistic.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.statistic.dto.dto.ActivityType;
import com.hcmus.mela.statistic.dto.response.GetStatisticsResponse;
import com.hcmus.mela.statistic.service.StatisticQueryService;
import com.hcmus.mela.statistic.strategy.StatisticFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticController {

    private final StatisticQueryService statisticQueryService;
    private final JwtTokenService jwtTokenService;
    private final Map<String, StatisticFilterStrategy> strategies;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{levelId}")
    @Operation(tags = "📊 Statistic Service", summary = "Get statistics",
            description = "Get statistics of user in the system.")
    public ResponseEntity<GetStatisticsResponse> getProfileStatisticsRequest(
            @Parameter(description = "Level Id", example = "c9dcb3d7-c80c-4431-afd7-c727c8e5ee5b")
            @PathVariable("levelId") UUID levelId,
            @Parameter(description = "Type of activity", example = "EXERCISE, TEST, LESSON, ALL, ...", required = false)
            @RequestParam(value = "type", required = false) String type,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        ActivityType activityType = ActivityType.fromValue(type);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Getting statistics for user {} with level {} and type {}", userId, levelId, activityType);
        GetStatisticsResponse response = statisticQueryService.getStatisticByUserIdAndLevelIdAndType(
                userId, levelId, activityType);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/{levelId}/users/{userId}")
    @Operation(tags = "📊 Statistic Service", summary = "Get statistics",
            description = "Get statistics of user in the system.")
    public ResponseEntity<GetStatisticsResponse> getUserStatisticsRequest(
            @PathVariable UUID levelId,
            @PathVariable UUID userId,
            @RequestParam(value = "type", required = false) String type,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        log.info("Getting statistics for user with level {} and type {}", levelId, type);
        ActivityType activityType = ActivityType.fromValue(type);
        UUID ownId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        StatisticFilterStrategy strategy = strategies.get("STATISTIC_" + userRole.toString().toUpperCase());
        GetStatisticsResponse response = statisticQueryService.getStatisticByUserIdAndLevelIdAndType(
                strategy, ownId, userId, levelId, activityType);
        return ResponseEntity.ok(response);
    }
}
