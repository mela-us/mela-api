package com.hcmus.mela.level.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.level.dto.request.CreateLevelRequest;
import com.hcmus.mela.level.dto.request.DenyLevelRequest;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.dto.response.CreateLevelResponse;
import com.hcmus.mela.level.dto.response.GetLevelsResponse;
import com.hcmus.mela.level.service.LevelCommandService;
import com.hcmus.mela.level.service.LevelQueryService;
import com.hcmus.mela.level.service.LevelStatusService;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/levels")
@Slf4j
public class LevelController {

    private final LevelQueryService levelQueryService;
    private final JwtTokenService jwtTokenService;
    private final LevelCommandService levelCommandService;
    private final LevelStatusService levelStatusService;
    private final Map<String, LevelFilterStrategy> strategies;

    @GetMapping
    @Operation(
            tags = "Math Category Service",
            summary = "Get all levels",
            description = "Retrieves all levels existing in the system."
    )
    public ResponseEntity<GetLevelsResponse> getLevelsRequest(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Getting levels in system");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authorizationHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        LevelFilterStrategy strategy = strategies.get("LEVEL_" + userRole.toString());
        GetLevelsResponse response = levelQueryService.getLevelsResponse(strategy, userId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PostMapping
    public ResponseEntity<CreateLevelResponse> createLevelRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateLevelRequest request) {
        log.info("Creating level {}", request.getName());
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        CreateLevelResponse response = levelCommandService.createLevel(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PutMapping("/{levelId}")
    public ResponseEntity<Map<String, String>> updateLevelRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateLevelRequest request,
            @PathVariable UUID levelId) {
        log.info("Updating level {}", levelId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LevelFilterStrategy strategy = strategies.get("LEVEL_" + userRole.toString().toUpperCase());
        levelCommandService.updateLevel(strategy, userId, levelId, request);
        return ResponseEntity.ok(Map.of("message", "Level updated successfully"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @DeleteMapping("/{levelId}")
    public ResponseEntity<Map<String, String>> deleteLevelRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID levelId) {
        log.info("Deleting level {}", levelId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LevelFilterStrategy strategy = strategies.get("LEVEL_" + userRole.toString().toUpperCase());
        levelCommandService.deleteLevel(strategy, userId, levelId);
        return ResponseEntity.ok(Map.of("message", "Level deleted successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{levelId}/deny")
    public ResponseEntity<Map<String, String>> denyLevelRequest(
            @PathVariable UUID levelId,
            @RequestBody DenyLevelRequest request) {
        log.info("Deny level {}", levelId);
        levelStatusService.denyLevel(levelId, request.getReason());
        return ResponseEntity.ok(Map.of("message", "Level denied successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{levelId}/approve")
    public ResponseEntity<Map<String, String>> approveLevelRequest(@PathVariable UUID levelId) {
        log.info("Approve level {}", levelId);
        levelStatusService.approveLevel(levelId);
        return ResponseEntity.ok(Map.of("message", "Level approved successfully"));
    }
}
