package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.model.Question;
import com.hcmus.mela.history.dto.dto.ExerciseAnswerDto;
import com.hcmus.mela.history.model.ExerciseAnswer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExerciseGradeService {

    List<ExerciseAnswer> gradeExercise(UUID exerciseId, List<ExerciseAnswerDto> answers);

    Map<String, Object> evaluateQuestion(ExerciseAnswerDto answer, Question question);
}
