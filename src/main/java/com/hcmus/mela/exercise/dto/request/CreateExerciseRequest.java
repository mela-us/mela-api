package com.hcmus.mela.exercise.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExerciseRequest {

    @NotNull(message = "Lecture id cannot be null")
    private UUID lectureId;

    @NotNull(message = "Exercise name cannot be null")
    private String exerciseName;

    @NotNull(message = "Ordinal number cannot be null")
    private Integer ordinalNumber;

    @NotNull(message = "Questions cannot be null")
    @NotEmpty(message = "Questions list cannot be empty")
    private List<@Valid CreateQuestionRequest> questions;
}
