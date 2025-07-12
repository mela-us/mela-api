package com.hcmus.mela.lecture.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.lecture.dto.request.CreateLectureRequest;
import com.hcmus.mela.lecture.dto.request.DenyLectureRequest;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.lecture.dto.response.*;
import com.hcmus.mela.lecture.service.LectureCommandService;
import com.hcmus.mela.lecture.service.LectureQueryService;
import com.hcmus.mela.lecture.service.LectureStatusService;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureQueryService lectureQueryService;
    private final LectureCommandService lectureCommandService;
    private final LectureStatusService lectureStatusService;
    private final JwtTokenService jwtTokenService;
    private final Map<String, LectureFilterStrategy> strategies;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/all")
    @Operation(tags = "🎓 Lecture Service", summary = "Get all lectures",
            description = "Retrieves all lectures in the system based on user role.")
    public ResponseEntity<GetAllLecturesResponse> getAllLecturesRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        log.info("Getting lectures in system");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authorizationHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString());
        GetAllLecturesResponse response = lectureQueryService.getAllLectures(strategy, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/{lectureId}")
    @Operation(tags = "🎓 Lecture Service", summary = "Get lecture info",
            description = "Retrieves detailed information about a specific lecture by its id.")
    public ResponseEntity<GetLectureInfoResponse> getLectureInfoRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID lectureId) {
        log.info("Getting lecture {}", lectureId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString().toUpperCase());
        GetLectureInfoResponse response = lectureQueryService.getLectureInfoByLectureId(strategy, userId, lectureId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PostMapping
    @Operation(tags = "🎓 Lecture Service", summary = "Create a lecture",
            description = "Creates a new lecture in the system.")
    public ResponseEntity<CreateLectureResponse> createLectureRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateLectureRequest request) {
        log.info("Creating lecture {}", request.getName());
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString().toUpperCase());
        CreateLectureResponse response = lectureCommandService.createLecture(strategy, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PutMapping("/{lectureId}")
    @Operation(tags = "🎓 Lecture Service", summary = "Update a lecture",
            description = "Updates an existing lecture in the system.")
    public ResponseEntity<UpdateLectureResponse> updateLectureRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateLectureRequest request,
            @PathVariable UUID lectureId) {
        log.info("Updating lecture {}", lectureId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString().toUpperCase());
        lectureCommandService.updateLecture(strategy, userId, lectureId, request);
        return ResponseEntity.ok(new UpdateLectureResponse("Lecture updated successfully"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @DeleteMapping("/{lectureId}")
    @Operation(tags = "🎓 Lecture Service", summary = "Delete a lecture",
            description = "Deletes an existing lecture from the system.")
    public ResponseEntity<DeleteLectureResponse> deleteLectureRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID lectureId) {
        log.info("Deleting lecture {}", lectureId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        LectureFilterStrategy strategy = strategies.get("LECTURE_" + userRole.toString().toUpperCase());
        lectureCommandService.deleteLecture(strategy, userId, lectureId);
        return ResponseEntity.ok(new DeleteLectureResponse("Lecture deleted successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{lectureId}/deny")
    @Operation(tags = "🎓 Lecture Service", summary = "Deny a lecture",
            description = "Denies a lecture request with a reason.")
    public ResponseEntity<DenyLectureResponse> denyLectureRequest(
            @PathVariable UUID lectureId,
            @RequestBody DenyLectureRequest request) {
        log.info("Deny lecture {}", lectureId);
        if (request.getReason() == null || request.getReason().isEmpty()) {
            request.setReason("Lecture denied without a specific reason");
        }
        lectureStatusService.denyLecture(lectureId, request.getReason());
        return ResponseEntity.ok(new DenyLectureResponse("Lecture denied successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{lectureId}/approve")
    @Operation(tags = "🎓 Lecture Service", summary = "Approve a lecture",
            description = "Approves a lecture request.")
    public ResponseEntity<ApproveLectureResponse> approveLectureRequest(@PathVariable UUID lectureId) {
        log.info("Approve lecture {}", lectureId);
        lectureStatusService.approveLecture(lectureId);
        return ResponseEntity.ok(new ApproveLectureResponse("Lecture approved successfully"));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(tags = "🎓 Lecture Service", summary = "Get lectures by level id",
            description = "Retrieves all lectures associated with the specified level id.")
    public ResponseEntity<GetLecturesByLevelResponse> getLecturesByLevelRequest(
            @RequestParam(value = "levelId") String levelId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Getting lectures for level {} and user {}", levelId, userId);
        GetLecturesByLevelResponse response = lectureQueryService.getLecturesByLevelId(userId, UUID.fromString(levelId));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/search")
    @Operation(tags = "🎓 Lecture Service", summary = "Search lectures by keyword",
            description = "Searches for lectures that match the given keyword in title or content.")
    public ResponseEntity<GetLecturesWithStatsResponse> getLecturesByKeywordRequest(
            @RequestParam(value = "q") String keyword,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Searching lectures with keyword '{}' for user {}", keyword, userId);
        GetLecturesWithStatsResponse response = lectureQueryService.getLecturesByKeyword(userId, keyword);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{lectureId}/sections")
    @Operation(tags = "🎓 Lecture Service", summary = "Get lecture sections",
            description = "Retrieves all sections of a specific lecture by its id.")
    public ResponseEntity<GetLectureSectionsResponse> getLectureSectionsRequest(
            @PathVariable String lectureId) {
        log.info("Getting sections for lecture {}", lectureId);
        GetLectureSectionsResponse response = lectureQueryService
                .getLectureSectionsByLectureId(UUID.fromString(lectureId));
        return ResponseEntity.ok(response);
    }
}
