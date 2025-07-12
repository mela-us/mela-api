package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.response.QuestionResponse;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.model.Question;

import java.util.UUID;

public interface ExerciseQuestionService {

    QuestionResponse findQuestionsByExerciseId(UUID exerciseId);

    Question findQuestionByQuestionId(UUID questionId);

    Exercise findExerciseByQuestionId(UUID questionId);

    void updateQuestionHint(Exercise exercise);
}
