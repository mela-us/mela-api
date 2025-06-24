package com.hcmus.mela.lecture.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.lecture.dto.response.GetLevelsResponse;
import com.hcmus.mela.lecture.service.LevelService;
import com.hcmus.mela.lecture.strategy.LevelFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
