package com.hcmus.mela.history.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.history.dto.request.ExerciseResultRequest;
import com.hcmus.mela.history.dto.response.ExerciseResultResponse;
import com.hcmus.mela.history.dto.response.GetUploadUrlResponse;
import com.hcmus.mela.history.service.ExerciseHistoryService;
import com.hcmus.mela.shared.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/exercise-histories")
public class ExerciseHistoryController {

    private final ExerciseHistoryService exerciseHistoryService;
    private final StorageService storageService;
    private final JwtTokenService jwtTokenService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(tags = "🧾 History Service", summary = "Submit exercise result",
            description = "Submit exercise result of the user in the system.")
    public ResponseEntity<ExerciseResultResponse> submitExerciseHistory(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ExerciseResultRequest request) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Saving exercise result for user {}", userId);
        ExerciseResultResponse response = exerciseHistoryService.getExerciseResultResponse(userId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/files/upload-url")
    @Operation(tags = "🧾 History Service", summary = "Get upload URL",
            description = "Get pre-signed URL to upload exercise files to the storage.")
    public ResponseEntity<GetUploadUrlResponse> getUploadUrl(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Generating upload URL for user {}", userId);
        String path = String.format("exercises/%s-%s-%s", LocalDate.now(), userId.toString().substring(3), UUID.randomUUID().toString().substring(3));
        Map<String, String> urls = storageService.getUploadUserFilePreSignedUrl(path);
        return ResponseEntity.ok(new GetUploadUrlResponse(
                urls.get("preSignedUrl"),
                urls.get("storedUrl")
        ));
    }
}
