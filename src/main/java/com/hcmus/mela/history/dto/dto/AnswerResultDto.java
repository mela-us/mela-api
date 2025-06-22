package com.hcmus.mela.history.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Setter
@Getter
public class AnswerResultDto {

    private UUID questionId;

    private Boolean isCorrect;

    private String feedback;
}