package com.hcmus.mela.ai.chat.controller;

import com.hcmus.mela.ai.chat.dto.request.QuestionConfusionRequestDto;
import com.hcmus.mela.ai.chat.dto.response.QuestionConfusionResponseDto;
import com.hcmus.mela.ai.chat.service.QuestionConfusionService;
import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chatbot/questions")
@RequiredArgsConstructor
public class QuestionHelperController {
    private final QuestionConfusionService questionConfusionService;
    private final JwtTokenService jwtTokenService;


    @PostMapping("/{questionId}")
    public ResponseEntity<QuestionConfusionResponseDto> resolveQuestionConfusion(
            @PathVariable String questionId,
            @Valid @RequestBody QuestionConfusionRequestDto requestDto,
            @RequestHeader(value = "Authorization") String authorizationHeader) {
        // Extract user id from JWT token
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        QuestionConfusionResponseDto responseDto = questionConfusionService.resolveQuestionConfusion(UUID.fromString(questionId), requestDto);

        return ResponseEntity.ok(responseDto);
    }
}
