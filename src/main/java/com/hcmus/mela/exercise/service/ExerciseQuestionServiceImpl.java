package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.dto.QuestionDto;
import com.hcmus.mela.exercise.dto.response.QuestionResponse;
import com.hcmus.mela.exercise.exception.ExerciseException;
import com.hcmus.mela.exercise.mapper.ExerciseMapper;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.model.Question;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseQuestionServiceImpl implements ExerciseQuestionService {

    private final ExerciseRepository exerciseRepository;

    @Override
    public QuestionResponse findQuestionsByExerciseId(UUID exerciseId, UUID userId) {
        Exercise exercise = exerciseRepository.findByExerciseIdAndStatus(exerciseId, ContentStatus.VERIFIED)
                .orElseThrow(() -> new ExerciseException("Exercise not found with id: " + exerciseId));
        ExerciseDto exerciseDto = ExerciseMapper.INSTANCE.exerciseToExerciseDto(exercise);
        List<QuestionDto> questionDtoList = exerciseDto.getQuestions();
        String message = String.format("Found %d questions for exercise with id: %s", questionDtoList.size(), exerciseId);
        log.info(message);
        return new QuestionResponse(message, questionDtoList.size(), questionDtoList);
    }

    @Override
    public Question findQuestionByQuestionId(UUID questionId) {
        Exercise exercise = findExerciseByQuestionId(questionId);
        if (exercise.getQuestions() == null || exercise.getQuestions().isEmpty() || questionId == null) {
            return null;
        }
        if (exercise.getStatus() != ContentStatus.VERIFIED) {
            log.warn("Exercise with id {} is not verified", exercise.getExerciseId());
            return null;
        }
        return exercise.getQuestions().stream()
                .filter(q -> q.getQuestionId().equals(questionId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Exercise findExerciseByQuestionId(UUID questionId) {
        return exerciseRepository.findByQuestionsQuestionIdAndStatus(questionId, ContentStatus.VERIFIED)
                .orElseThrow(() -> new ExerciseException("Exercise not found for question id: " + questionId));
    }

    @Override
    public void updateQuestionHint(Exercise exercise) {
        Exercise result = exerciseRepository.updateQuestionHint(exercise);
        if (result == null) {
            log.warn("Exercise {} not found", exercise.getExerciseId());
            return;
        }
        log.debug("Exercise {} updated successfully", result.getExerciseId());
    }
}
