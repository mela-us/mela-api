package com.hcmus.mela.ai.chat.service;

import com.hcmus.mela.ai.chat.dto.request.LectureConfusionRequestDto;
import com.hcmus.mela.ai.chat.dto.response.LectureConfusionResponseDto;
import com.hcmus.mela.ai.client.builder.AiRequestBodyFactory;
import com.hcmus.mela.ai.client.config.AiClientProperties;
import com.hcmus.mela.ai.client.config.AiFeatureProperties;
import com.hcmus.mela.ai.client.filter.AiResponseFilter;
import com.hcmus.mela.ai.client.prompts.LectureConfusionPrompt;
import com.hcmus.mela.ai.client.webclient.AiWebClient;
import org.springframework.stereotype.Service;

@Service
public class LectureConfusionServiceImpl implements LectureConfusionService {
    private final AiWebClient aiWebClient;
    private final AiFeatureProperties aiClientProperties;
    private final AiRequestBodyFactory aiRequestBodyFactory;
    private final LectureConfusionPrompt lectureConfusionPrompt;

    public LectureConfusionServiceImpl(
            AiWebClient aiWebClient,
            AiClientProperties aiClientProperties,
            AiRequestBodyFactory aiRequestBodyFactory,
            LectureConfusionPrompt lectureConfusionPrompt
    ) {
        this.aiWebClient = aiWebClient;
        this.aiClientProperties = aiClientProperties.getChatBot();
        this.aiRequestBodyFactory = aiRequestBodyFactory;
        this.lectureConfusionPrompt = lectureConfusionPrompt;
    }


    @Override
    public LectureConfusionResponseDto resolveLectureConfusion(LectureConfusionRequestDto requestDto) {
        Object requestBody = aiRequestBodyFactory.buildRequestBodyForLectureConfusion(
                lectureConfusionPrompt.getInstruction(),
                requestDto.getText(),
                requestDto.getImageUrl(),
                requestDto.getFileUrl(),
                requestDto.getCurrentPage(),
                aiClientProperties);

        Object response = aiWebClient.fetchAiResponse(aiClientProperties, requestBody);
        AiResponseFilter aiResponseFilter = new AiResponseFilter();
        return new LectureConfusionResponseDto(aiResponseFilter.getMessage(response));
    }
}
