package com.hcmus.mela.exercise.repository;

import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.shared.type.ContentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExerciseCustomRepository {

    Exercise updateQuestionHint(Exercise exercise);
}
