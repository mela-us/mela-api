package com.hcmus.mela.ai.client.builder;

import com.hcmus.mela.ai.chat.model.Message;
import com.hcmus.mela.ai.client.config.AiFeatureProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Factory for creating AI request bodies based on the provider.
 * This class determines which implementation of AiRequestBodyBuilder to use
 * based on the provider specified in the AI feature properties.
 */
@Component
public class AiRequestBodyFactory {
    private final Map<String, AiRequestBodyBuilder> requestBodyBuilders;

    /**
     * Constructor that injects all available request body builders.
     * The builders are mapped by bean name, which should follow the pattern
     * "{providerName}RequestBodyBuilder".
     *
     * @param requestBodyBuilders Map of request body builders injected by Spring
     */
    public AiRequestBodyFactory(Map<String, AiRequestBodyBuilder> requestBodyBuilders) {
        this.requestBodyBuilders = requestBodyBuilders;
    }

    public Object createRequestBodyForQuestionHint(String prompt, String textData, List<String> imageUrls, AiFeatureProperties aiFeatureProperties) {
        AiRequestBodyBuilder builder = requestBodyBuilders.get(aiFeatureProperties.getProvider() + "RequestBodyBuilder");

        if (builder == null) {
            throw new IllegalArgumentException("Unknown provider: " + aiFeatureProperties.getProvider() + ". Available providers: " + requestBodyBuilders.keySet());
        }

        return builder.buildRequestBodyForQuestionHint(prompt, textData, imageUrls, aiFeatureProperties);
    }

    public Object createRequestBodyForChatBot(String prompt, List<Message> messages, AiFeatureProperties aiFeatureProperties) {
        AiRequestBodyBuilder builder = requestBodyBuilders.get(aiFeatureProperties.getProvider() + "RequestBodyBuilder");

        if (builder == null) {
            throw new IllegalArgumentException("Unknown provider: " + aiFeatureProperties.getProvider() + ". Available providers: " + requestBodyBuilders.keySet());
        }

        return builder.buildRequestBodyForChatBot(prompt, messages, aiFeatureProperties);
    }

    public Object createRequestBodyForAiGrader(String prompt, String question, String solution, String assignmentText, List<String> assignmentImageUrls, AiFeatureProperties aiFeatureProperties) {
        AiRequestBodyBuilder builder = requestBodyBuilders.get(aiFeatureProperties.getProvider() + "RequestBodyBuilder");

        if (builder == null) {
            throw new IllegalArgumentException("Unknown provider: " + aiFeatureProperties.getProvider() + ". Available providers: " + requestBodyBuilders.keySet());
        }

        return builder.buildRequestBodyForAiGrader(prompt, question, solution, assignmentText, assignmentImageUrls, aiFeatureProperties);
    }

    public Object buildRequestBodyForQuestionConfusion(String prompt, String textData, List<String> imageUrls, AiFeatureProperties aiFeatureProperties) {
        AiRequestBodyBuilder builder = requestBodyBuilders.get(aiFeatureProperties.getProvider() + "RequestBodyBuilder");

        if (builder == null) {
            throw new IllegalArgumentException("Unknown provider: " + aiFeatureProperties.getProvider() + ". Available providers: " + requestBodyBuilders.keySet());
        }

        return builder.buildRequestBodyForQuestionConfusion(prompt, textData, imageUrls, aiFeatureProperties);
    }

    public Object buildRequestBodyForLectureConfusion(String prompt, String textData, String imageUrl, String fileUrl, Integer currentPage, AiFeatureProperties aiFeatureProperties) {
        AiRequestBodyBuilder builder = requestBodyBuilders.get(aiFeatureProperties.getProvider() + "RequestBodyBuilder");

        if (builder == null) {
            throw new IllegalArgumentException("Unknown provider: " + aiFeatureProperties.getProvider() + ". Available providers: " + requestBodyBuilders.keySet());
        }

        return builder.buildRequestBodyForLectureConfusion(prompt, textData, imageUrl, fileUrl, currentPage, aiFeatureProperties);
    }
}
