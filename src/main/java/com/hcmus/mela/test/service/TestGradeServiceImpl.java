package com.hcmus.mela.test.service;

import com.hcmus.mela.ai.client.builder.AiRequestBodyFactory;
import com.hcmus.mela.ai.client.config.AiClientProperties;
import com.hcmus.mela.ai.client.filter.AiResponseFilter;
import com.hcmus.mela.ai.client.prompts.AiGraderPrompt;
import com.hcmus.mela.ai.client.webclient.AiWebClient;
import com.hcmus.mela.history.dto.dto.TestAnswerDto;
import com.hcmus.mela.history.exception.HistoryException;
import com.hcmus.mela.history.mapper.TestAnswerMapper;
import com.hcmus.mela.history.model.TestAnswer;
import com.hcmus.mela.shared.async.AsyncCustomService;
import com.hcmus.mela.shared.utils.TextUtils;
import com.hcmus.mela.test.model.Option;
import com.hcmus.mela.test.model.Question;
import com.hcmus.mela.test.model.QuestionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TestGradeServiceImpl implements TestGradeService {

    private final float CORRECT_SCORE = 0.7f;
    private final AiWebClient aiWebClient;
    private final AiClientProperties.AiGrader aiGraderProperties;
    private final AiGraderPrompt aiGraderPrompt;
    private final AiRequestBodyFactory aiRequestBodyFactory;
    private final AiResponseFilter aiResponseFilter;
    private final AsyncCustomService asyncService;
    private final TestService testService;

    public TestGradeServiceImpl(AiWebClient aiWebClient,
                                AiClientProperties aiClientProperties,
                                AiGraderPrompt aiGraderPrompt,
                                AiRequestBodyFactory aiRequestBodyFactory,
                                AiResponseFilter aiResponseFilter,
                                AsyncCustomService asyncService,
                                TestService testService) {
        this.aiWebClient = aiWebClient;
        this.aiGraderProperties = aiClientProperties.getAiGrader();
        this.aiGraderPrompt = aiGraderPrompt;
        this.aiRequestBodyFactory = aiRequestBodyFactory;
        this.aiResponseFilter = aiResponseFilter;
        this.asyncService = asyncService;
        this.testService = testService;
    }

    @Override
    public List<TestAnswer> gradeTest(List<TestAnswerDto> testAnswerList) {
        List<CompletableFuture<TestAnswer>> answersFutures = new ArrayList<>();
        for (TestAnswerDto TestAnswerDto : testAnswerList) {
            TestAnswer answer = TestAnswerMapper.INSTANCE.testAnswerDtoToTestAnswer(TestAnswerDto);
            answer.setFeedback("");
            answer.setIsCorrect(false);
            CompletableFuture<TestAnswer> future = asyncService.runComplexAsync(
                    () -> {
                        Map<String, Object> result = checkQuestionAnswer(TestAnswerDto);
                        answer.setIsCorrect((Boolean) result.get("isCorrect"));
                        answer.setFeedback((String) result.get("feedback"));
                        return answer;
                    },
                    answer
            );
            answersFutures.add(future);
        }
        CompletableFuture.allOf(answersFutures.toArray(new CompletableFuture[0])).join();
        return answersFutures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Map<String, Object> checkQuestionAnswer(TestAnswerDto answerDto) {
        Question questionResult = testService.getQuestionById(answerDto.getQuestionId());

        if (questionResult == null) {
            throw new HistoryException("Question does not exist.");
        }

        if (questionResult.getQuestionType() == QuestionType.FILL_IN_THE_BLANK) {
            return checkBlankAnswer(answerDto, questionResult);
        } else if (questionResult.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            return checkMultipleChoiceAnswer(answerDto, questionResult);
        } else if (questionResult.getQuestionType() == QuestionType.ESSAY) {
            return checkEssayAnswer(answerDto, questionResult);
        } else {
            throw new HistoryException("Unsupported question type " + questionResult.getQuestionType());
        }
    }

    private Map<String, Object> checkMultipleChoiceAnswer(TestAnswerDto answerDto, Question question) {
        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            throw new HistoryException("Question multiple choice options is empty.");
        }
        if (answerDto.getSelectedOption() == null) {
            return Map.of(
                    "isCorrect", false,
                    "feedback", "Học sinh chưa nhập câu trả lời.");
        }
        Option option = question.getOptions().stream()
                .filter(opt -> opt.getOrdinalNumber() == answerDto.getSelectedOption().intValue())
                .findFirst()
                .orElse(null);
        if (option != null && option.getIsCorrect()) {
            return Map.of(
                    "isCorrect", true,
                    "feedback", "");
        }
        return Map.of(
                "isCorrect", false,
                "feedback", "");
    }

    private Map<String, Object> checkBlankAnswer(TestAnswerDto answerDto, Question question) {
        if (question.getBlankAnswer() == null || question.getBlankAnswer().isEmpty()) {
            throw new HistoryException("Question blank answer is empty.");
        }
        String normalizedAnswer = TextUtils.normalizeText(answerDto.getBlankAnswer());
        String normalizedSolution = TextUtils.normalizeText(question.getBlankAnswer());
        return Map.of(
                "isCorrect", normalizedAnswer.equalsIgnoreCase(normalizedSolution),
                "feedback", "");
    }

    private Map<String, Object> checkEssayAnswer(TestAnswerDto answerDto, Question question) {
        Object requestBody = aiRequestBodyFactory.createRequestBodyForAiGrader(
                aiGraderPrompt.formatInstruction(CORRECT_SCORE),
                question.getContent(),
                question.getSolution(),
                answerDto.getBlankAnswer(),
                answerDto.getImages(),
                aiGraderProperties);

        Object responseObject = aiWebClient.fetchAiResponse(aiGraderProperties, requestBody);
        String responseText = aiResponseFilter.getMessage(responseObject);
        Map<String, Object> jsonResponse = TextUtils.extractResponseFromJsonText(responseText, "score", "feedback");
        if (jsonResponse.get("score") != null) {
            float score = Float.parseFloat(jsonResponse.get("score").toString());
            String feedback = jsonResponse.get("feedback") != null ? jsonResponse.get("feedback").toString() : "";
            if (score >= CORRECT_SCORE) {
                return Map.of(
                        "isCorrect", true,
                        "feedback", feedback);
            } else {
                return Map.of(
                        "isCorrect", false,
                        "feedback", feedback);
            }
        } else {
            String feedback = jsonResponse.get("feedback") != null ? jsonResponse.get("feedback").toString() : "";
            return Map.of(
                    "isCorrect", false,
                    "feedback", feedback);
        }
    }
}
