package com.hcmus.mela.ai.chat.service;

import com.hcmus.mela.ai.chat.dto.request.QuestionConfusionRequestDto;
import com.hcmus.mela.ai.chat.dto.response.QuestionConfusionResponseDto;

import java.util.UUID;

public interface QuestionConfusionService {

    QuestionConfusionResponseDto resolveQuestionConfusion(UUID questionId, QuestionConfusionRequestDto questionConfusionRequestDto);
}
