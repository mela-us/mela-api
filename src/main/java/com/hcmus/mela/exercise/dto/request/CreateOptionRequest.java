package com.hcmus.mela.exercise.dto.request;

import com.hcmus.mela.exercise.model.QuestionType;
import com.hcmus.mela.lecture.model.SectionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOptionRequest {

    @NotNull(message = "Ordinal number cannot be null")
    private Integer ordinalNumber;

    @NotNull(message = "Content cannot be null")
    private String content;

    @NotNull(message = "Is correct cannot be null")
    private Boolean isCorrect;
}
