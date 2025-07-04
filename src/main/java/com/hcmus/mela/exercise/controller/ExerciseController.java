package com.hcmus.mela.exercise.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.exercise.dto.request.CreateExerciseRequest;
import com.hcmus.mela.exercise.dto.request.DenyExerciseRequest;
import com.hcmus.mela.exercise.dto.request.ExerciseRequest;
import com.hcmus.mela.exercise.dto.request.UpdateExerciseRequest;
import com.hcmus.mela.exercise.dto.response.*;
import com.hcmus.mela.exercise.service.ExerciseCommandService;
import com.hcmus.mela.exercise.service.ExerciseQueryService;
import com.hcmus.mela.exercise.service.ExerciseQuestionService;
import com.hcmus.mela.exercise.service.ExerciseStatusService;
import com.hcmus.mela.exercise.strategy.ExerciseFilterStrategy;
import com.hcmus.mela.user.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api")
public class ExerciseController {

    private final ExerciseQuestionService exerciseQuestionService;
    private final ExerciseQueryService exerciseQueryService;
    private final ExerciseStatusService exerciseStatusService;
    private final ExerciseCommandService exerciseCommandService;
    private final JwtTokenService jwtTokenService;
    private final Map<String, ExerciseFilterStrategy> strategies;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/lectures/{lectureId}/exercises")
    @Operation(
            tags = "Exercise Service",
            summary = "Get sections",
            description = "Retrieves a list of exercises belonging to a lecture from the system."
    )
    public ResponseEntity<GetExercisesInLectureResponse> getExerciseInLecture(
            @Parameter(description = "Lecture id", example = "54c3abc5-3e8b-4017-acc2-c1005cd51c28")
            @PathVariable String lectureId,
            @RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        ExerciseRequest exerciseRequest = new ExerciseRequest(null, UUID.fromString(lectureId), userId);

        log.info("Getting exercises for lecture: {}", lectureId);
        final GetExercisesInLectureResponse getExercisesInLectureResponse = exerciseQueryService.getExercisesByLectureId(UUID.fromString(lectureId), userId);

        return ResponseEntity.ok(getExercisesInLectureResponse);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/exercises/{exerciseId}")
    @Operation(
            tags = "Exercise Service",
            summary = "Get questions",
            description = "Retrieves a list of questions belonging to an exercise from the system."
    )
    public ResponseEntity<QuestionResponse> getQuestions(
            @Parameter(description = "Exercise id", example = "b705289f-888d-44be-a894-c7d0db1cec67")
            @PathVariable String exerciseId,
            @RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        ExerciseRequest exerciseRequest = new ExerciseRequest(UUID.fromString(exerciseId), null, userId);

        log.info("Getting questions for exercise: {}", exerciseId);
        final QuestionResponse exerciseResponse = exerciseQuestionService.findQuestionsByExerciseId(UUID.fromString(exerciseId), userId);

        return ResponseEntity.ok(exerciseResponse);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping(value = "/exercises")
    public ResponseEntity<GetAllExercisesResponse> getAllExercisesRequest(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Getting exercises in system");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authorizationHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        ExerciseFilterStrategy strategy = strategies.get("EXERCISE_" + userRole.toString());
        GetAllExercisesResponse response = exerciseQueryService.getAllExercises(strategy, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PostMapping(value = "/exercises")
    public ResponseEntity<CreateExerciseResponse> createExerciseRequest(
            @RequestBody CreateExerciseRequest request,
            @RequestHeader("Authorization") String authHeader) {
        log.info("Creating exercise {}", request.getExerciseName());
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ExerciseFilterStrategy strategy = strategies.get("EXERCISE_" + userRole.toString().toUpperCase());
        CreateExerciseResponse response = exerciseCommandService.createExercise(strategy, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PutMapping(value = "/exercises/{exerciseId}")
    public ResponseEntity<Map<String, String>> updateExerciseRequest(
            @PathVariable UUID exerciseId,
            @RequestBody UpdateExerciseRequest request,
            @RequestHeader("Authorization") String authHeader) {
        log.info("Updating exercise {}", exerciseId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ExerciseFilterStrategy strategy = strategies.get("EXERCISE_" + userRole.toString().toUpperCase());
        exerciseCommandService.updateExercise(strategy, userId, exerciseId, request);
        return ResponseEntity.ok(Map.of("message", "Exercise updated successfully"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @DeleteMapping("/exercises/{exerciseId}")
    public ResponseEntity<Map<String, String>> deleteExerciseRequest(
            @PathVariable UUID exerciseId,
            @RequestHeader("Authorization") String authHeader) {
        log.info("Deleting exercise {}", exerciseId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ExerciseFilterStrategy strategy = strategies.get("EXERCISE_" + userRole.toString().toUpperCase());
        exerciseCommandService.deleteExercise(strategy, userId, exerciseId);
        return ResponseEntity.ok(Map.of("message", "Exercise deleted successfully"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/exercises/{exerciseId}/info")
    public ResponseEntity<GetExerciseInfoResponse> getExerciseInfoRequest(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID exerciseId) {
        log.info("Get exercise information");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authorizationHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        ExerciseFilterStrategy strategy = strategies.get("EXERCISE_" + userRole.toString());
        GetExerciseInfoResponse response = exerciseQueryService.getExerciseInfoByExerciseId(strategy, userId, exerciseId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/exercises/{exerciseId}/deny")
    public ResponseEntity<Map<String, String>> denyExerciseRequest(
            @PathVariable UUID exerciseId,
            @RequestBody DenyExerciseRequest request) {
        log.info("Deny exercise {}", exerciseId);
        exerciseStatusService.denyExercise(exerciseId, request.getReason());
        return ResponseEntity.ok(Map.of("message", "Exercise denied successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/exercises/{exerciseId}/approve")
    public ResponseEntity<Map<String, String>> approveExerciseRequest(@PathVariable UUID exerciseId) {
        log.info("Approve exercise {}", exerciseId);
        exerciseStatusService.approveExercise(exerciseId);
        return ResponseEntity.ok(Map.of("message", "Exercise approved successfully"));
    }
}