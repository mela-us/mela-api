package com.hcmus.mela.ai.client.builder;


import com.hcmus.mela.ai.client.config.AiFeatureProperties;
import com.hcmus.mela.ai.client.dto.request.AzureMessage;
import com.hcmus.mela.ai.client.dto.request.AzureRequestBody;
import com.hcmus.mela.shared.utils.PdfExtractor;
import com.hcmus.mela.shared.utils.TextUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    public Object buildRequestBodyForChatBot(String prompt, List<com.hcmus.mela.ai.chat.model.Message> message, AiFeatureProperties aiFeatureProperties) {

        List<AzureMessage> inputMessages = new ArrayList<>();

        for (com.hcmus.mela.ai.chat.model.Message msg : message) {
            String role = msg.getRole();
            List<Map<String, Object>> inputContents = new ArrayList<>();
            Map<String, Object> content = msg.getContent();
            Map<String, Object> imgContent;

            StringBuilder textContentBuilder = new StringBuilder();
            for (Map.Entry<String, Object> entry : content.entrySet()) {
                if (entry.getKey().equals("image_url")) {
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
    public Object buildRequestBodyForAiGrader(String prompt, String question, String solution, String assignmentText, List<String> assignmentImageUrls, AiFeatureProperties aiFeatureProperties) {
        List<Map<String, Object>> questionUserMessage = new ArrayList<>();
        List<Map<String, Object>> guideUserMessage = new ArrayList<>();
        List<Map<String, Object>> answerUserMessage = new ArrayList<>();

        questionUserMessage.add(Map.of("type", "text", "text", "Đây là nội dung câu hỏi (có thể gồm text và hình ảnh)."));
        // Add text content if provided
        if (question != null && !question.isBlank()) {
            questionUserMessage.add(Map.of("type", "text", "text", "Nội dung câu hỏi: " + question));
        }
        List<String> questionImageUrls = TextUtils.extractImageSources(question);
        // Add image URLs if provided
        if (!questionImageUrls.isEmpty()) {
            for (String imageUrl : questionImageUrls) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    questionUserMessage.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl.trim())));
                }
            }
        }

        guideUserMessage.add(Map.of("type", "text", "text", "Đây là nội dung hướng dẫn trả lời (có thể gồm text và hình ảnh)."));
        if (solution != null && !solution.isBlank()) {
            guideUserMessage.add(Map.of("type", "text", "text", "Nội dung câu trả lời: " + solution));
        }
        List<String> solutionImageUrls = TextUtils.extractImageSources(solution);
        // Add image URLs if provided
        if (!solutionImageUrls.isEmpty()) {
            for (String imageUrl : solutionImageUrls) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    guideUserMessage.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl.trim())));
                }
            }
        }

        answerUserMessage.add(Map.of("type", "text", "text", "Đây là lời giải học sinh cung cấp (có thể gồm text và hình ảnh)."));
        if (assignmentText != null && !assignmentText.isBlank()) {
            answerUserMessage.add(Map.of("type", "text", "text", "Dạng text: " + assignmentText));
        }
        // Add image URLs if provided
        if (assignmentImageUrls != null && !assignmentImageUrls.isEmpty()) {
            for (String imageUrl : assignmentImageUrls) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    answerUserMessage.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl.trim())));
                }
            }
        }

        // Create the full request body with system prompt and user content
        return new AzureRequestBody(
                aiFeatureProperties.getModel(),
                List.of(
                        new AzureMessage("developer", prompt),
                        new AzureMessage("user", questionUserMessage),
                        new AzureMessage("user", guideUserMessage),
                        new AzureMessage("user", answerUserMessage)
                )
        );
    }

    @Override
    public Object buildRequestBodyForQuestionConfusion(String prompt, String textData, List<String> imageUrls, AiFeatureProperties aiFeatureProperties) {
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
    public Object buildRequestBodyForLectureConfusion(
            String prompt,
            String textData,
            String imageUrl,
            String fileUrl,
            Integer currentPage,
            AiFeatureProperties aiFeatureProperties
    ) {
        List<Map<String, Object>> contentList = new ArrayList<>();

        if (textData != null && !textData.isBlank()) {
            contentList.add(Map.of("type", "text", "text", textData));
        }

        if (imageUrl != null && !imageUrl.isBlank()) {
            contentList.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl.trim())));
        }

        if (fileUrl != null && !fileUrl.isBlank()) {
            try {
                Integer startPage = Math.max(currentPage - 2, 1);
                Integer endPage = currentPage + 2;
                PdfExtractor.ExtractedPdf doc = PdfExtractor.extractFromUrl(fileUrl, startPage, endPage);

                if (doc.text != null && !doc.text.isBlank()) {
                    contentList.add(Map.of("type", "text", "text", doc.text));
                }

                for (String imgData : doc.imageBase64) {
                    contentList.add(Map.of("type", "image_url", "image_url", Map.of("url", imgData)));
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to fetch or extract PDF from: " + fileUrl, e);
            }
        }

        return new AzureRequestBody(
                aiFeatureProperties.getModel(),
                List.of(
                        new AzureMessage("developer", prompt),
                        new AzureMessage("user", contentList)
                )
        );
    }


}
