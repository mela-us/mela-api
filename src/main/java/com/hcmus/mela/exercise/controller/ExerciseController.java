package com.hcmus.mela.exercise.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.exercise.dto.request.CreateExerciseRequest;
import com.hcmus.mela.exercise.dto.request.DenyExerciseRequest;
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

@Slf4j
@RestController
@AllArgsConstructor
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
    @Operation(tags = "💯 Exercise Service", summary = "Get exercises in lecture",
            description = "Retrieves a list of exercises belonging to a lecture from the system.")
    public ResponseEntity<GetExercisesInLectureResponse> getExerciseInLecture(
            @PathVariable String lectureId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Getting exercises for lecture {}", lectureId);
        final GetExercisesInLectureResponse response = exerciseQueryService
                .getExercisesByLectureId(UUID.fromString(lectureId), userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/exercises/{exerciseId}")
    @Operation(tags = "💯 Exercise Service", summary = "Get questions in exercise",
            description = "Retrieves a list of questions belonging to an exercise from the system.")
    public ResponseEntity<QuestionResponse> getQuestions(
            @PathVariable String exerciseId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Getting questions for exercise {}", exerciseId);
        final QuestionResponse exerciseResponse = exerciseQuestionService.findQuestionsByExerciseId(UUID.fromString(exerciseId), userId);
        return ResponseEntity.ok(exerciseResponse);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping(value = "/exercises")
    @Operation(tags = "💯 Exercise Service", summary = "Get all exercises",
            description = "Retrieves a list of exercises from the system.")
    public ResponseEntity<GetAllExercisesResponse> getAllExercisesRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        log.info("Getting exercises in system");
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ExerciseFilterStrategy strategy = strategies.get("EXERCISE_" + userRole.toString());
        GetAllExercisesResponse response = exerciseQueryService.getAllExercises(strategy, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @GetMapping("/exercises/{exerciseId}/info")
    @Operation(tags = "💯 Exercise Service", summary = "Get exercise info",
            description = "Retrieves the info of a exercise from the system.")
    public ResponseEntity<GetExerciseInfoResponse> getExerciseInfoRequest(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID exerciseId) {
        log.info("Getting exercise information of {}", exerciseId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ExerciseFilterStrategy strategy = strategies.get("EXERCISE_" + userRole.toString());
        GetExerciseInfoResponse response = exerciseQueryService.getExerciseInfoByExerciseId(strategy, userId, exerciseId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @PostMapping(value = "/exercises")
    @Operation(tags = "💯 Exercise Service", summary = "Create exercise",
            description = "Creates a new exercise in the system.")
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
    @Operation(tags = "💯 Exercise Service", summary = "Update exercise",
            description = "Updates an existing exercise in the system.")
    public ResponseEntity<UpdateExerciseResponse> updateExerciseRequest(
            @PathVariable UUID exerciseId,
            @RequestBody UpdateExerciseRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        log.info("Updating exercise {}", exerciseId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ExerciseFilterStrategy strategy = strategies.get("EXERCISE_" + userRole.toString().toUpperCase());
        exerciseCommandService.updateExercise(strategy, userId, exerciseId, request);
        return ResponseEntity.ok(new UpdateExerciseResponse("Update exercise successfully"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CONTRIBUTOR')")
    @DeleteMapping("/exercises/{exerciseId}")
    @Operation(tags = "💯 Exercise Service", summary = "Delete exercise",
            description = "Deletes an existing exercise from the system.")
    public ResponseEntity<DeleteExerciseResponse> deleteExerciseRequest(
            @PathVariable UUID exerciseId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        log.info("Deleting exercise {}", exerciseId);
        UserRole userRole = jwtTokenService.getRoleFromAuthorizationHeader(authHeader);
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        ExerciseFilterStrategy strategy = strategies.get("EXERCISE_" + userRole.toString().toUpperCase());
        exerciseCommandService.deleteExercise(strategy, userId, exerciseId);
        return ResponseEntity.ok(new DeleteExerciseResponse("Delete exercise successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/exercises/{exerciseId}/deny")
    @Operation(tags = "💯 Exercise Service", summary = "Deny exercise request",
            description = "Denies an exercise request with a reason.")
    public ResponseEntity<DenyExerciseResponse> denyExerciseRequest(
            @PathVariable UUID exerciseId,
            @RequestBody DenyExerciseRequest request) {
        log.info("Deny exercise {}", exerciseId);
        exerciseStatusService.denyExercise(exerciseId, request.getReason());
        return ResponseEntity.ok(new DenyExerciseResponse("Exercise denied successfully"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/exercises/{exerciseId}/approve")
    @Operation(tags = "💯 Exercise Service", summary = "Approve exercise request",
            description = "Approves an exercise request.")
    public ResponseEntity<ApproveExerciseResponse> approveExerciseRequest(@PathVariable UUID exerciseId) {
        log.info("Approve exercise {}", exerciseId);
        exerciseStatusService.approveExercise(exerciseId);
        return ResponseEntity.ok(new ApproveExerciseResponse("Exercise approved successfully"));
    }
}