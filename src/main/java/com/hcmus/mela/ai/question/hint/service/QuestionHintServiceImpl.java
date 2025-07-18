package com.hcmus.mela.ai.question.hint.service;

import com.hcmus.mela.ai.client.builder.AiRequestBodyFactory;
import com.hcmus.mela.ai.client.config.AiClientProperties;
import com.hcmus.mela.ai.client.filter.AiResponseFilter;
import com.hcmus.mela.ai.client.prompts.QuestionHintPrompt;
import com.hcmus.mela.ai.client.webclient.AiWebClient;
import com.hcmus.mela.ai.question.hint.dto.response.HintResponseDto;
import com.hcmus.mela.ai.question.hint.exception.QuestionHintException;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.model.Option;
import com.hcmus.mela.exercise.model.Question;
import com.hcmus.mela.exercise.service.ExerciseQuestionService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.service.LectureInfoService;
import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.service.LevelInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class QuestionHintServiceImpl implements QuestionHintService {

    private final QuestionHintPrompt questionHintPrompt;
    private final AiWebClient aiWebClient;
    private final AiClientProperties.QuestionHint questionHintProperties;
    private final AiRequestBodyFactory aiRequestBodyFactory;
    private final AiResponseFilter aiResponseFilter;
    private final ExerciseQuestionService exerciseQuestionService;
    private final LevelInfoService levelInfoService;
    private final LectureInfoService lectureInfoService;

    public QuestionHintServiceImpl(QuestionHintPrompt questionHintPrompt,
                                   ExerciseQuestionService exerciseQuestionService,
                                   AiWebClient aiWebClient,
                                   AiClientProperties aiClientProperties,
                                   AiRequestBodyFactory aiRequestBodyFactory,
                                   AiResponseFilter aiResponseFilter,
                                   LectureInfoService lectureInfoService,
                                   LevelInfoService levelInfoService) {
        this.questionHintPrompt = questionHintPrompt;
        this.exerciseQuestionService = exerciseQuestionService;
        this.aiWebClient = aiWebClient;
        this.questionHintProperties = aiClientProperties.getQuestionHint();
        this.aiRequestBodyFactory = aiRequestBodyFactory;
        this.aiResponseFilter = aiResponseFilter;
        this.lectureInfoService = lectureInfoService;
        this.levelInfoService = levelInfoService;
    }

    public List<String> generateKeys(UUID questionId) {
        Question question = exerciseQuestionService.findQuestionByQuestionId(questionId);
        Exercise exercise = exerciseQuestionService.findExerciseByQuestionId(questionId);
        LectureDto lecture = lectureInfoService.findLectureByLectureId(exercise.getLectureId());
        LevelDto level = levelInfoService.findLevelByLevelId(lecture.getLevelId());

        List<String> keys = new ArrayList<>();
        keys.add(level.getName());
        keys.add(question.getContent());

        String answer = "Lời giải: " + question.getGuide();
        if (question.getBlankAnswer() != null) {
            answer += ("\nĐáp án: " + question.getBlankAnswer());
        } else {
            answer += "\nĐáp án: ";
            for (Option option : question.getOptions()) {
                if (option.getIsCorrect()) {
                    answer += option.getContent();
                    break;
                }
            }
        }

        keys.add(answer);
        return keys;
    }

    public List<String> generateTemplate(Map<String, String> instruction,
                                         Map<String, String> userMessage,
                                         List<String> keys) {
        List<String> template = new ArrayList<>();
        String task = instruction.get("task").replace("{level}", keys.get(0))
                .replace("{question}", keys.get(1))
                .replace("{answer}", keys.get(2));
        String background = instruction.get("background").replace("{level}", keys.get(0))
                .replace("{question}", keys.get(1))
                .replace("{answer}", keys.get(2));
        String requirement = instruction.get("requirement").replace("{level}", keys.get(0))
                .replace("{question}", keys.get(1))
                .replace("{answer}", keys.get(2));
        template.add(task + "\n" + background + "\n" + requirement);
        template.add(userMessage.get("data")
                .replace("{level}", keys.get(0))
                .replace("{question}", keys.get(1))
                .replace("{answer}", keys.get(2)));
        return template;
    }

    @Override
    public HintResponseDto generateTerms(UUID questionId) {
        Question question = exerciseQuestionService.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new QuestionHintException("Verified question not found with id " + questionId);
        }

        if (question.getTerms() == null || question.getTerms().isEmpty()) {
            List<String> keys = generateKeys(questionId);

            Map<String, String> instruction = questionHintPrompt.getTerms().get("instruction");
            Map<String, String> userMessage = questionHintPrompt.getTerms().get("userMessage");

            List<String> termRequest = generateTemplate(instruction, userMessage, keys);
            List<String> imgSrcs = extractImageSources(termRequest.get(1));

            Object requestBody = aiRequestBodyFactory.createRequestBodyForQuestionHint(
                    termRequest.get(0),
                    termRequest.get(1),
                    imgSrcs,
                    questionHintProperties);

            Object response = aiWebClient.fetchAiResponse(questionHintProperties, requestBody);
            String responseText = aiResponseFilter.getMessage(response);

            Exercise exercise = exerciseQuestionService.findExerciseByQuestionId(questionId);
            List<Question> questions = exercise.getQuestions();
            for (Question q : questions) {
                if (q.getQuestionId().equals(questionId)) {
                    question.setTerms(responseText);
                    q.setTerms(responseText);
                    break;
                }
            }
            exercise.setQuestions(questions);
            exerciseQuestionService.updateQuestionHint(exercise);
        }
        return new HintResponseDto(question.getTerms());
    }

    @Override
    public HintResponseDto generateGuide(UUID questionId) {
        Question question = exerciseQuestionService.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new QuestionHintException("Verified question not found with id " + questionId);
        }

        if (question.getGuide() == null || question.getGuide().isEmpty()) {
            List<String> keys = generateKeys(questionId);

            Map<String, String> instruction = questionHintPrompt.getGuide().get("instruction");
            Map<String, String> userMessage = questionHintPrompt.getGuide().get("userMessage");

            List<String> guideRequest = generateTemplate(instruction, userMessage, keys);
            List<String> imgSrcs = extractImageSources(guideRequest.get(1));

            Object requestBody = aiRequestBodyFactory.createRequestBodyForQuestionHint(
                    guideRequest.get(0),
                    guideRequest.get(1),
                    imgSrcs,
                    questionHintProperties);

            Object response = aiWebClient.fetchAiResponse(questionHintProperties, requestBody);
            String responseText = aiResponseFilter.getMessage(response);

            Exercise exercise = exerciseQuestionService.findExerciseByQuestionId(questionId);
            List<Question> questions = exercise.getQuestions();
            for (Question q : questions) {
                if (q.getQuestionId().equals(questionId)) {
                    question.setGuide(responseText);
                    q.setGuide(responseText);
                    break;
                }
            }
            exercise.setQuestions(questions);
            exerciseQuestionService.updateQuestionHint(exercise);
        }
        return new HintResponseDto(question.getGuide());
    }

    private List<String> extractImageSources(String text) {
        List<String> imageSources = new ArrayList<>();
        String regex = "<img\\s+src='([^\"]*)'>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            imageSources.add(matcher.group(1)); // Capture the src value
        }

        return imageSources;
    }
}
