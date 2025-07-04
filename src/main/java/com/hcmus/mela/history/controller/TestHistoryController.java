package com.hcmus.mela.history.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.history.dto.request.TestResultRequest;
import com.hcmus.mela.history.dto.response.TestResultResponse;
import com.hcmus.mela.history.service.TestHistoryService;
import com.hcmus.mela.shared.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    @Operation(
            tags = "History Service",
            summary = "Save test result",
            description = "Save test result of the user in the system."
    )
    public ResponseEntity<TestResultResponse> saveTestHistory(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody TestResultRequest request) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        log.info("Saving test result for user: {}", userId);

        TestResultResponse response = testHistoryService.getTestResultResponse(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/files/upload-url")
    public ResponseEntity<Map<String, String>> getUploadUrl(@RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        String path = String.format("tests/%s-%s-%s", LocalDate.now().toString(), userId.toString().substring(3), UUID.randomUUID().toString().substring(5));

        Map<String, String> urls = storageService.getUploadUserFilePreSignedUrl(path);

        return ResponseEntity.ok().body(
                Map.of("preSignedUrl", urls.get("preSignedUrl"), "fileUrl", urls.get("storedUrl"))
        );
    }
}
