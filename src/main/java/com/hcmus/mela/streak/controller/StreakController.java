package com.hcmus.mela.streak.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.streak.dto.response.GetStreakResponse;
import com.hcmus.mela.streak.dto.response.UpdateStreakResponse;
import com.hcmus.mela.streak.service.StreakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/streak")
public class StreakController {

    private final StreakService streakService;
    private final JwtTokenService jwtTokenService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(tags = "🔥 Streak Service", summary = "Get user's streak",
            description = "Retrieves a user's streak and the information belonging to the streak.")
    public ResponseEntity<GetStreakResponse> getStreakRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Getting streak for user {}", userId);
        final GetStreakResponse response = streakService.getStreak(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(tags = "🔥 Streak Service", summary = "Update user's streak",
            description = "Updates a user's streak.")
    public ResponseEntity<UpdateStreakResponse> updateStreakRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Updating streak for user {}", userId);
        final UpdateStreakResponse updateStreakResponse = streakService.updateStreak(userId);
        return ResponseEntity.ok(updateStreakResponse);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/{userId}")
    @Operation(tags = "🔥 Streak Service", summary = "Get streak by user ID",
            description = "Retrieves a user's streak by their user ID. Only accessible by ADMIN and CONTRIBUTOR roles.")
    public ResponseEntity<GetStreakResponse> getStreakOfUserRequest(@PathVariable UUID userId) {
        log.info("Getting streak of user {}", userId);
        final GetStreakResponse response = streakService.getStreak(userId);
        return ResponseEntity.ok(response);
    }
}
