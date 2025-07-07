package com.hcmus.mela.ai.chat.controller;

import com.hcmus.mela.ai.chat.dto.request.QuestionConfusionRequestDto;
import com.hcmus.mela.ai.chat.dto.response.QuestionConfusionResponseDto;
import com.hcmus.mela.ai.chat.service.QuestionConfusionService;
import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot/questions")
public class QuestionHelperController {

    private final QuestionConfusionService questionConfusionService;
    private final JwtTokenService jwtTokenService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{questionId}")
    @Operation(tags = "❓ Confusion Service", summary = "Resolve question confusion",
            description = "Resolve confusion about exercise questions.")
    public ResponseEntity<QuestionConfusionResponseDto> resolveQuestionConfusion(
            @PathVariable String questionId,
            @Valid @RequestBody QuestionConfusionRequestDto request) {
        QuestionConfusionResponseDto response = questionConfusionService
                .resolveQuestionConfusion(UUID.fromString(questionId), request);
        return ResponseEntity.ok(response);
    }
}
