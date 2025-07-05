package com.hcmus.mela.history.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.history.dto.request.SaveSectionRequest;
import com.hcmus.mela.history.dto.response.SaveSectionResponse;
import com.hcmus.mela.history.service.LectureHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/lecture-histories")
public class LectureHistoryController {

    private final LectureHistoryService lectureHistoryService;
    private final JwtTokenService jwtTokenService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping()
    @Operation(
            tags = "History Service",
            summary = "Save section",
            description = "Save learning section of user in the system."
    )
    public ResponseEntity<SaveSectionResponse> saveLectureSectionHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SaveSectionRequest request) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Saving lecture section for user {}", userId);
        SaveSectionResponse response = lectureHistoryService.saveSection(userId, request);
        return ResponseEntity.ok(response);
    }
}
