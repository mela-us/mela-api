package com.hcmus.mela.ai.chat.controller;

import com.hcmus.mela.ai.chat.dto.request.LectureConfusionRequestDto;
import com.hcmus.mela.ai.chat.dto.response.LectureConfusionResponseDto;
import com.hcmus.mela.ai.chat.service.LectureConfusionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot/lectures")
public class LectureHelperController {

    private final LectureConfusionService lectureConfusionService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(tags = "❓ Confusion Service", summary = "Resolve section confusion",
            description = "Resolve confusion about section questions.")
    public ResponseEntity<LectureConfusionResponseDto> resolveQuestionConfusion(
            @Valid @RequestBody LectureConfusionRequestDto request) {
        LectureConfusionResponseDto response = lectureConfusionService.resolveLectureConfusion(request);
        return ResponseEntity.ok(response);
    }
}
