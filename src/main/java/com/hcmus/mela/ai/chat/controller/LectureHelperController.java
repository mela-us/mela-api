package com.hcmus.mela.ai.chat.controller;

import com.hcmus.mela.ai.chat.dto.request.LectureConfusionRequestDto;
import com.hcmus.mela.ai.chat.dto.response.LectureConfusionResponseDto;
import com.hcmus.mela.ai.chat.service.LectureConfusionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot/lectures")
@RequiredArgsConstructor
public class LectureHelperController {
    private final LectureConfusionService lectureConfusionService;

    @PostMapping("")
    public ResponseEntity<LectureConfusionResponseDto> resolveQuestionConfusion(
            @Valid @RequestBody LectureConfusionRequestDto requestDto) {
        LectureConfusionResponseDto responseDto = lectureConfusionService.resolveLectureConfusion(requestDto);

        return ResponseEntity.ok(responseDto);
    }
}
