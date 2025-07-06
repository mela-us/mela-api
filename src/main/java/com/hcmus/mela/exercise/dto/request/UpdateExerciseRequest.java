package com.hcmus.mela.exercise.dto.request;

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
public class UpdateExerciseRequest {

    private UUID lectureId;

    private String exerciseName;

    private Integer ordinalNumber;

    private List<UpdateQuestionRequest> questions;
}
