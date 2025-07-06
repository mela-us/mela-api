package com.hcmus.mela.history.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.history.dto.request.TestResultRequest;
import com.hcmus.mela.history.dto.response.GetUploadUrlResponse;
import com.hcmus.mela.history.dto.response.TestResultResponse;
import com.hcmus.mela.history.service.TestHistoryService;
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
@RequestMapping("/api/test-histories")
public class TestHistoryController {

    private final TestHistoryService testHistoryService;
    private final StorageService storageService;
    private final JwtTokenService jwtTokenService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(tags = "🧾 History Service", summary = "Submit test result",
            description = "Submit test result of the user in the system.")
    public ResponseEntity<TestResultResponse> saveTestHistory(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody TestResultRequest request) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authHeader);
        log.info("Saving test result for user {}", userId);
        TestResultResponse response = testHistoryService.getTestResultResponse(userId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/files/upload-url")
    @Operation(tags = "🧾 History Service", summary = "Get upload URL",
            description = "Get pre-signed URL to upload test files to the storage.")
    public ResponseEntity<GetUploadUrlResponse> getUploadUrl(@RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);
        String path = String.format("tests/%s-%s-%s", LocalDate.now(), userId.toString().substring(3), UUID.randomUUID().toString().substring(3));
        Map<String, String> urls = storageService.getUploadUserFilePreSignedUrl(path);
        return ResponseEntity.ok(new GetUploadUrlResponse(
                urls.get("preSignedUrl"),
                urls.get("storedUrl")
        ));
    }
}
