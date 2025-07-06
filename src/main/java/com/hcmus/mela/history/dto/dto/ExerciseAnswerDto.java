package com.hcmus.mela.history.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Setter
@Getter
public class ExerciseAnswerDto {

    private UUID questionId;

    private String blankAnswer;

    private Integer selectedOption;

    private List<String> images;

    private Boolean isCorrect;
}