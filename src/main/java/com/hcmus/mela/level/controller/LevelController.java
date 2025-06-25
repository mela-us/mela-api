package com.hcmus.mela.level.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.level.dto.request.CreateLevelRequest;
import com.hcmus.mela.level.dto.request.DenyLevelRequest;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.dto.response.CreateLevelResponse;
import com.hcmus.mela.level.dto.response.GetLevelsResponse;
import com.hcmus.mela.level.service.LevelService;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final LevelService levelService;

    private final JwtTokenService jwtTokenService;

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
        GetLevelsResponse response = levelService.getLevelsResponse(strategy, userId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PostMapping
    public ResponseEntity<CreateLevelResponse> createLevelRequest(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateLevelRequest createLevelRequest) {
        log.info("Creating level");
        UUID creatorId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        CreateLevelResponse response = levelService.getCreateLevelResponse(creatorId, createLevelRequest);

        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PutMapping("/{levelId}")
    public ResponseEntity<Map<String, String>> updateLevelRequest(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UpdateLevelRequest updateLevelRequest,
            @PathVariable UUID levelId) {
        log.info("Updating level");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authorizationHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        LevelFilterStrategy strategy = strategies.get("TOPIC_" + userRole.toString());
        levelService.updateLevel(strategy, userId, levelId, updateLevelRequest);

        return ResponseEntity.ok(
                Map.of("message", "Level updated successfully")
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{levelId}/deny")
    public ResponseEntity<Map<String, String>> denyLevelRequest(@PathVariable UUID levelId, @RequestBody DenyLevelRequest denyLevelRequest) {
        log.info("Deny level");
        levelService.denyLevel(levelId, denyLevelRequest.getReason());
        return ResponseEntity.ok(
                Map.of("message", "Level denied successfully")
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{levelId}/approve")
    public ResponseEntity<Map<String, String>> approveLevelRequest(@PathVariable UUID levelId) {
        log.info("Approve level");
        levelService.approveLevel(levelId);
        return ResponseEntity.ok(
                Map.of("message", "Level approved successfully")
        );
    }
}
