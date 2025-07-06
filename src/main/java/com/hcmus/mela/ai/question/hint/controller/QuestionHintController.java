package com.hcmus.mela.ai.question.hint.controller;

import com.hcmus.mela.ai.question.hint.dto.response.HintResponseDto;
import com.hcmus.mela.ai.question.hint.service.QuestionHintService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class QuestionHintController {

    private final QuestionHintService questionHintService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{questionId}/hint/terms")
    @Operation(tags = "📗 Question Hint Service", summary = "Generate terms",
            description = "Generate terms for a question.")
    public ResponseEntity<HintResponseDto> generateTerms(@PathVariable UUID questionId) {
        HintResponseDto hint = questionHintService.generateTerms(questionId);
        return ResponseEntity.ok(hint);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{questionId}/hint/guide")
    @Operation(tags = "📗 Question Hint Service", summary = "Generate guide",
            description = "Generate guide for a question.")
    public ResponseEntity<HintResponseDto> generateGuide(@PathVariable UUID questionId) {
        HintResponseDto hint = questionHintService.generateGuide(questionId);
        return ResponseEntity.ok(hint);
    }
}
