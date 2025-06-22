package com.hcmus.mela.exercise.service;

import com.hcmus.mela.ai.client.builder.AiRequestBodyFactory;
import com.hcmus.mela.ai.client.config.AiClientProperties;
import com.hcmus.mela.ai.client.filter.AiResponseFilter;
import com.hcmus.mela.ai.client.prompts.AiGraderPrompt;
import com.hcmus.mela.ai.client.webclient.AiWebClient;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.model.Option;
import com.hcmus.mela.exercise.model.Question;
import com.hcmus.mela.exercise.model.QuestionType;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.history.dto.dto.ExerciseAnswerDto;
import com.hcmus.mela.history.mapper.ExerciseAnswerMapper;
import com.hcmus.mela.history.model.ExerciseAnswer;
import com.hcmus.mela.shared.async.AsyncCustomService;
import com.hcmus.mela.shared.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ExerciseGradeServiceImpl implements ExerciseGradeService {

    private final float CORRECT_SCORE = 0.7f;
    private final AiWebClient aiWebClient;
    private final AiClientProperties.AiGrader aiGraderProperties;
    private final AiGraderPrompt aiGraderPrompt;
    private final AiRequestBodyFactory aiRequestBodyFactory;
    private final AiResponseFilter aiResponseFilter;
    private final ExerciseRepository exerciseRepository;
    private final AsyncCustomService asyncService;

    public ExerciseGradeServiceImpl(AiWebClient aiWebClient,
                                    AiClientProperties aiClientProperties,
                                    AiGraderPrompt aiGraderPrompt,
                                    AiRequestBodyFactory aiRequestBodyFactory,
                                    ExerciseRepository exerciseRepository,
                                    AiResponseFilter aiResponseFilter,
                                    AsyncCustomService asyncService) {
        this.aiWebClient = aiWebClient;
        this.aiGraderProperties = aiClientProperties.getAiGrader();
        this.aiGraderPrompt = aiGraderPrompt;
        this.aiRequestBodyFactory = aiRequestBodyFactory;
        this.exerciseRepository = exerciseRepository;
        this.aiResponseFilter = aiResponseFilter;
        this.asyncService = asyncService;
    }

    @Override
    public List<ExerciseAnswer> gradeExercise(UUID exerciseId, List<ExerciseAnswerDto> exerciseAnswerList) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found with id: " + exerciseId));
        if (exercise.getQuestions() == null || exercise.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("Exercise does not contain any questions.");
        }
        List<Question> questions = exercise.getQuestions();

        List<CompletableFuture<ExerciseAnswer>> answersFutures = new ArrayList<>();
        for (ExerciseAnswerDto exerciseAnswerDto : exerciseAnswerList) {
            ExerciseAnswer answer = ExerciseAnswerMapper.INSTANCE.convertToExerciseAnswer(exerciseAnswerDto);
            answer.setFeedback("");
            answer.setIsCorrect(false);
            CompletableFuture<ExerciseAnswer> future = asyncService.runComplexAsync(
                    () -> {
                        if (exerciseAnswerDto.getQuestionId() == null) {
                            throw new IllegalArgumentException("Answer must have a question ID.");
                        }
                        Question question = questions.stream()
                                .filter(q -> q.getQuestionId().equals(exerciseAnswerDto.getQuestionId()))
                                .findFirst()
                                .orElse(null);
                        if (question != null) {
                            Map<String, Object> result = checkQuestionAnswer(exerciseAnswerDto, question);
                            answer.setIsCorrect((Boolean) result.get("isCorrect"));
                            answer.setFeedback((String) result.get("feedback"));
                        }
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

    public Map<String, Object> checkQuestionAnswer(ExerciseAnswerDto answerDto, Question question) {
        if (question.getQuestionType() == QuestionType.FILL_IN_THE_BLANK) {
            return checkBlankAnswer(answerDto, question);
        } else if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            return checkMultipleChoiceAnswer(answerDto, question);
        } else if (question.getQuestionType() == QuestionType.ESSAY) {
            return checkEssayAnswer(answerDto, question);
        } else {
            throw new IllegalArgumentException("Unsupported question type: " + question.getQuestionType());
        }
    }

    private Map<String, Object> checkMultipleChoiceAnswer(ExerciseAnswerDto answerDto, Question question) {
        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            return Map.of("isCorrect", false, "feedback", "");
        }
        if (answerDto.getSelectedOption() == null || answerDto.getSelectedOption() < 1 || answerDto.getSelectedOption() > question.getOptions().size()) {
            return Map.of("isCorrect", false, "feedback", "");
        }
        Option option = question.getOptions().stream()
                .filter(opt -> opt.getOrdinalNumber() == answerDto.getSelectedOption().intValue())
                .findFirst()
                .orElse(null);
        if (option != null && option.getIsCorrect()) {
            return Map.of("isCorrect", true, "feedback", "");
        }
        return Map.of("isCorrect", false, "feedback", "");
    }

    private Map<String, Object> checkBlankAnswer(ExerciseAnswerDto answerDto, Question question) {
        if (question.getBlankAnswer() == null || question.getBlankAnswer().isEmpty()) {
            return Map.of("isCorrect", false, "feedback", "");
        }
        String normalizedAnswer = TextUtils.normalizeText(answerDto.getBlankAnswer());
        String normalizedSolution = TextUtils.normalizeText(question.getBlankAnswer());
        return Map.of("isCorrect", normalizedAnswer.equalsIgnoreCase(normalizedSolution), "feedback", "");
    }

    private Map<String, Object> checkEssayAnswer(ExerciseAnswerDto answerDto, Question question) {
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
                return Map.of("isCorrect", true, "feedback", feedback);
            } else {
                return Map.of("isCorrect", false, "feedback", feedback);
            }
        } else {
            String feedback = jsonResponse.get("feedback") != null ? jsonResponse.get("feedback").toString() : "";
            return Map.of("isCorrect", false, "feedback", feedback);
        }
    }
}
