package com.hcmus.mela.ai.chat.service;

import com.hcmus.mela.ai.chat.dto.request.LectureConfusionRequestDto;
import com.hcmus.mela.ai.chat.dto.response.LectureConfusionResponseDto;

public interface LectureConfusionService {

    LectureConfusionResponseDto resolveLectureConfusion(LectureConfusionRequestDto requestDto);
}
