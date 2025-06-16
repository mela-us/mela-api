package com.hcmus.mela.ai.client.builder;


import com.hcmus.mela.ai.client.config.AiFeatureProperties;
import com.hcmus.mela.ai.client.dto.request.AzureRequestBody;
import com.hcmus.mela.ai.client.dto.request.AzureMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Azure-specific implementation of AiRequestBodyBuilder.
 * Formats requests according to the Azure OpenAI API specifications.
 */
@Component
public class AzureRequestBodyBuilder implements AiRequestBodyBuilder {
    @Override
    public AzureRequestBody buildRequestBodyForQuestionHint(String prompt, String textData, List<String> imageUrls, AiFeatureProperties aiFeatureProperties) {
        List<Map<String, Object>> contentList = new ArrayList<>();

        // Add text content if provided
        if (textData != null && !textData.isBlank()) {
            contentList.add(Map.of("type", "text", "text", textData));
        }

        // Add image URLs if provided
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    contentList.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl.trim())));
                }
            }
        }

        // Create the full request body with system prompt and user content
        return new AzureRequestBody(
                aiFeatureProperties.getModel(),
                List.of(
                        new AzureMessage("developer", prompt),
                        new AzureMessage("user", contentList)
                )
        );
    }

    @Override
    public Object buildRequestBodyForChatBot(String prompt, List<com.hcmus.mela.ai.chatbot.model.Message> message, AiFeatureProperties aiFeatureProperties) {

        List<AzureMessage> inputMessages = new ArrayList<>();

        for (com.hcmus.mela.ai.chatbot.model.Message msg : message) {
            String role = msg.getRole();
            List<Map<String, Object>> inputContents = new ArrayList<>();
            Map<String, Object> content = msg.getContent();
            Map<String, Object> imgContent;

            StringBuilder textContentBuilder = new StringBuilder();
            for(Map.Entry<String, Object> entry : content.entrySet()) {
                if(entry.getKey().equals("imageUrl")) {
                    imgContent = Map.of("type", "image_url", "image_url", Map.of("url", entry.getValue()));
                    inputContents.add(imgContent);
                } else {
                    textContentBuilder.append(entry).append("\n");
                }
            }
            Map<String, Object> textContent = Map.of("type", "text", "text", textContentBuilder.toString());
            inputContents.add(textContent);
            inputMessages.add(new AzureMessage(role, inputContents));
        }

        // Create the full request body with system prompt and user content
        inputMessages.add(0, new AzureMessage("developer", prompt));
        return new AzureRequestBody(
                aiFeatureProperties.getModel(),
                inputMessages
        );
    }

    @Override
    public Object buildRequestBodyForAiGrader(String prompt, String questionText, List<String> questionImageUrls, String barem, String assignmentText, List<String> assignmentImageUrls, AiFeatureProperties aiFeatureProperties) {
        List<Map<String, Object>> userMessage = new ArrayList<>();
        // Add text content if provided
        if (questionText != null && !questionText.isBlank()) {
            userMessage.add(Map.of("type", "text", "text", "Đây là nội dung câu hỏi: " + questionText));
        }

        if (barem != null && !barem.isBlank()) {
            userMessage.add(Map.of("type", "text", "text", "Đây là barem chấm bài: " + questionText));
        }

        // Add image URLs if provided
        if (questionImageUrls != null && !questionImageUrls.isEmpty()) {
            for (String imageUrl : questionImageUrls) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    userMessage.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl.trim())));
                }
            }
        }

        if (assignmentText != null && !assignmentText.isBlank()) {
            userMessage.add(Map.of("type", "text", "text", "Đây là bài làm dạng text: " + assignmentText));
        }

        // Add image URLs if provided
        if (assignmentImageUrls != null && !assignmentImageUrls.isEmpty()) {
            for (String imageUrl : assignmentImageUrls) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    userMessage.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl.trim())));
                }
            }
        }

        // Create the full request body with system prompt and user content
        return new AzureRequestBody(
                aiFeatureProperties.getModel(),
                List.of(
                        new AzureMessage("developer", prompt),
                        new AzureMessage("user", userMessage)
                )
        );
    }
}
