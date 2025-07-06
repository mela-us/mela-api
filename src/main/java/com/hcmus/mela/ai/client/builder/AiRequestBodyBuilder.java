package com.hcmus.mela.ai.client.builder;

import com.hcmus.mela.ai.chat.model.Message;
import com.hcmus.mela.ai.client.config.AiFeatureProperties;

import java.util.List;

/**
 * Interface for building request bodies for different AI providers.
 * Each AI provider implementation should provide its own implementation
 * of this interface to format requests according to the provider's API.
 */
public interface AiRequestBodyBuilder {

    Object buildRequestBodyForQuestionHint(String prompt, String textData, List<String> imageUrls, AiFeatureProperties aiFeatureProperties);

    Object buildRequestBodyForChatBot(String prompt, List<Message> message, AiFeatureProperties aiFeatureProperties);

    Object buildRequestBodyForAiGrader(String prompt, String question, String solution, String assignmentText, List<String> assignmentImageUrls, AiFeatureProperties aiFeatureProperties);

    Object buildRequestBodyForQuestionConfusion(String prompt, String textData, List<String> imageUrls, AiFeatureProperties aiFeatureProperties);

    Object buildRequestBodyForLectureConfusion(String prompt, String textData, String imageUrl, String fileUrl, Integer currentPage, AiFeatureProperties aiFeatureProperties);
}