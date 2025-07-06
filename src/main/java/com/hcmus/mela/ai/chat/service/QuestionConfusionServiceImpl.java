package com.hcmus.mela.ai.chat.service;

import com.hcmus.mela.ai.chat.dto.request.QuestionConfusionRequestDto;
import com.hcmus.mela.ai.chat.dto.response.QuestionConfusionResponseDto;
import com.hcmus.mela.ai.chat.exception.ChatBotException;
import com.hcmus.mela.ai.client.builder.AiRequestBodyFactory;
import com.hcmus.mela.ai.client.config.AiClientProperties;
import com.hcmus.mela.ai.client.config.AiFeatureProperties;
import com.hcmus.mela.ai.client.filter.AiResponseFilter;
import com.hcmus.mela.ai.client.prompts.QuestionConfusionPrompt;
import com.hcmus.mela.ai.client.webclient.AiWebClient;
import com.hcmus.mela.exercise.model.Option;
import com.hcmus.mela.exercise.model.Question;
import com.hcmus.mela.exercise.model.QuestionType;
import com.hcmus.mela.exercise.service.ExerciseQuestionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuestionConfusionServiceImpl implements QuestionConfusionService {

    private final ExerciseQuestionService exerciseQuestionService;
    private final AiWebClient aiWebClient;
    private final AiFeatureProperties aiClientProperties;
    private final AiRequestBodyFactory aiRequestBodyFactory;
    private final QuestionConfusionPrompt questionConfusionPrompt;

    public QuestionConfusionServiceImpl(
            ExerciseQuestionService exerciseQuestionService,
            AiWebClient aiWebClient,
            AiClientProperties aiClientProperties,
            AiRequestBodyFactory aiRequestBodyFactory,
            QuestionConfusionPrompt questionConfusionPrompt
    ) {
        this.exerciseQuestionService = exerciseQuestionService;
        this.aiWebClient = aiWebClient;
        this.aiClientProperties = aiClientProperties.getChatBot();
        this.aiRequestBodyFactory = aiRequestBodyFactory;
        this.questionConfusionPrompt = questionConfusionPrompt;
    }

    private List<String> extractImageSources(String text) {
        List<String> imageSources = new ArrayList<>();
        String regex = "<img\\s+src='([^\"]*)'>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            imageSources.add(matcher.group(1));
        }
        return imageSources;
    }

    @Override
    public QuestionConfusionResponseDto resolveQuestionConfusion(UUID questionId, QuestionConfusionRequestDto requestDto) {
        Question question = exerciseQuestionService.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new ChatBotException("Question with id " + questionId + " not found");
        }

        StringBuilder textData = new StringBuilder("Đề bài: ").append(question.getContent());
        List<String> imageSources = new ArrayList<>(extractImageSources(question.getContent()));

        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            for (Option option : question.getOptions()) {
                textData.append(option.getContent());
                imageSources.addAll(extractImageSources(option.getContent()));
            }
        }

        textData.append("Lời giải của hệ thống: ").append(question.getSolution());

        String prompt;
        switch (requestDto.getType()) {
            case CUSTOM -> {
                prompt = questionConfusionPrompt.getCustomText().getInstruction();
                textData.append("Thắc mắc của học sinh: ").append(requestDto.getText());
                if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isBlank()) {
                    textData.append("Link ảnh học sinh gửi: ").append(requestDto.getImageUrl());
                    imageSources.add(requestDto.getImageUrl());
                }
            }
            case CLARIFY_QUESTION -> prompt = questionConfusionPrompt.getClarifyQuestion().getInstruction();
            case EXPLAIN_SOLUTION -> prompt = questionConfusionPrompt.getExplainSolution().getInstruction();
            default -> throw new ChatBotException("Unsupported type: " + requestDto.getType());
        }

        Object requestBody = aiRequestBodyFactory.buildRequestBodyForQuestionConfusion(
                prompt,
                textData.toString(),
                imageSources,
                aiClientProperties);

        Object response = aiWebClient.fetchAiResponse(aiClientProperties, requestBody);
        AiResponseFilter aiResponseFilter = new AiResponseFilter();
        return new QuestionConfusionResponseDto(aiResponseFilter.getMessage(response));
    }
}
