package com.hcmus.mela.history.dto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Id of the question", example = "9d299317-16c0-4e0b-acbd-0a914782efd5")
    private UUID questionId;

    @Schema(description = "Answer of the question", example = "")
    private String blankAnswer;

    @Schema(description = "Selected option", example = "4")
    private Integer selectedOption;

    @Schema(description = "Images of the blank answer", example = "'https://example.com/image1.png'")
    private List<String> images;

    @Schema(description = "Is the answer correct (not required)", example = "true")
    private Boolean isCorrect;
}