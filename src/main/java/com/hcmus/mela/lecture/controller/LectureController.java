package com.hcmus.mela.lecture.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.lecture.dto.request.CreateLectureRequest;
import com.hcmus.mela.lecture.dto.request.DenyLectureRequest;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.lecture.dto.response.*;
import com.hcmus.mela.lecture.exception.LectureException;
import com.hcmus.mela.lecture.service.LectureCommandService;
import com.hcmus.mela.lecture.service.LectureQueryService;
import com.hcmus.mela.lecture.service.LectureStatusService;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/lectures")
@Slf4j
public class LectureController {

    private final LectureQueryService lectureQueryService;
    private final LectureCommandService lectureCommandService;
    private final LectureStatusService lectureStatusService;
    private final JwtTokenService jwtTokenService;
    private final Map<String, LectureFilterStrategy> strategies;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/all")
    public ResponseEntity<GetAllLecturesResponse> getAllLecturesRequest(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Getting lectures in system");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authorizationHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString());
        GetAllLecturesResponse response = lectureQueryService.getAllLectures(strategy, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PostMapping
    public ResponseEntity<CreateLectureResponse> createLectureRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateLectureRequest request) {
        log.info("Creating lecture {}", request.getName());
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString().toUpperCase());
        CreateLectureResponse response = lectureCommandService.createLecture(strategy, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PutMapping("/{lectureId}")
    public ResponseEntity<Map<String, String>> updateLectureRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateLectureRequest request,
            @PathVariable UUID lectureId) {
        log.info("Updating lecture {}", lectureId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString().toUpperCase());
        lectureCommandService.updateLecture(strategy, userId, lectureId, request);
        return ResponseEntity.ok(Map.of("message", "Lecture updated successfully"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<Map<String, String>> deleteLectureRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID lectureId) {
        log.info("Delete lecture {}", lectureId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString().toUpperCase());
        lectureCommandService.deleteLecture(strategy, userId, lectureId);
        return ResponseEntity.ok(Map.of("message", "Lecture deleted successfully"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/{lectureId}")
    public ResponseEntity<GetLectureInfoResponse> getLectureInfoRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID lectureId) {
        log.info("Getting lecture {}", lectureId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString().toUpperCase());
        GetLectureInfoResponse response = lectureQueryService.getLectureInfoByLectureId(strategy, userId, lectureId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{lectureId}/deny")
    public ResponseEntity<Map<String, String>> denyLectureRequest(
            @PathVariable UUID lectureId,
            @RequestBody DenyLectureRequest request) {
        log.info("Deny lecture {}", lectureId);
        lectureStatusService.denyLecture(lectureId, request.getReason());
        return ResponseEntity.ok(Map.of("message", "Lecture denied successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{lectureId}/approve")
    public ResponseEntity<Map<String, String>> approveLectureRequest(@PathVariable UUID lectureId) {
        log.info("Approve lecture {}", lectureId);
        lectureStatusService.approveLecture(lectureId);
        return ResponseEntity.ok(Map.of("message", "Lecture approved successfully"));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(
            tags = "Lecture Service",
            summary = "Get lectures by level ID",
            description = "Retrieves all lectures associated with the specified level ID."
    )
    public ResponseEntity<GetLecturesByLevelResponse> getLecturesByLevelRequest(
            @Parameter(description = "Level ID (UUID format)", example = "a7e03165-05fc-4e82-b69b-2874aa006caf")
            @RequestParam(value = "levelId") String levelId,
            @RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        UUID levelUuid = UUID.fromString(levelId);

        log.info("Getting lectures for level: {} and user: {}", levelId, userId);
        GetLecturesByLevelResponse response = lectureQueryService.getLecturesByLevelId(userId, levelUuid);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/search")
    @Operation(
            tags = "Lecture Service",
            summary = "Search lectures by keyword",
            description = "Searches for lectures that match the given keyword in title or content."
    )
    public ResponseEntity<GetLecturesWithStatsResponse> getLecturesByKeywordRequest(
            @Parameter(description = "Search keyword", example = "math")
            @RequestParam(value = "q") String keyword,
            @RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        log.info("Searching lectures with keyword: '{}' for user: {}", keyword, userId);
        GetLecturesWithStatsResponse response = lectureQueryService.getLecturesByKeyword(userId, keyword);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/recent")
    @Operation(
            tags = "Lecture Service",
            summary = "Get recent lectures",
            description = "Retrieves the most recent lectures up to the specified size limit."
    )
    public ResponseEntity<GetLecturesWithStatsResponse> getLecturesByRecentRequest(
            @Parameter(description = "Number of recent lectures to retrieve", example = "5")
            @RequestParam(value = "size") Integer size,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (size <= 0) {
            throw new LectureException("Size parameter must be a positive integer");
        }
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        log.info("Getting {} recent lectures for user: {}", size, userId);
        GetLecturesWithStatsResponse response = lectureQueryService.getLecturesByRecent(userId, size);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{lectureId}/sections")
    @Operation(
            tags = "Lecture Section Service",
            summary = "Get lecture sections",
            description = "Retrieves all sections associated with the specified lecture ID."
    )
    public ResponseEntity<GetLectureSectionsResponse> getLectureSectionsRequest(
            @Parameter(description = "Lecture ID (UUID format)", example = "54c3abc5-3e8b-4017-acc2-c1005cd51c28")
            @PathVariable String lectureId) {
        log.info("Getting sections for lecture: {}", lectureId);
        GetLectureSectionsResponse response = lectureQueryService.getLectureSectionsByLectureId(UUID.fromString(lectureId));
        return ResponseEntity.ok(response);
    }
}
