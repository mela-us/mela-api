package com.hcmus.mela.ai.client.builder;

import com.hcmus.mela.ai.chatbot.model.Message;
import com.hcmus.mela.ai.client.config.AiFeatureProperties;

import java.util.List;

/**
 * Interface for building request bodies for different AI providers.
 * Each AI provider implementation should provide its own implementation
 * of this interface to format requests according to the provider's API.
 */
public interface AiRequestBodyBuilder {
    Object buildRequestBodyForQuestionHint(String instruction, String textData, List<String> imageUrls, AiFeatureProperties aiFeatureProperties);
    Object buildRequestBodyForChatBot(String instruction, List<Message> message, AiFeatureProperties aiFeatureProperties);
    Object buildRequestBodyForAiGrader(String instruction, String textAssignment, List<String> imageUrlsAssignment, AiFeatureProperties aiFeatureProperties);
}