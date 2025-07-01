package com.hcmus.mela.exercise.dto.request;

import com.hcmus.mela.exercise.model.QuestionType;
import com.hcmus.mela.lecture.dto.request.CreateSectionRequest;
import com.hcmus.mela.lecture.model.SectionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {

    @NotNull(message = "Question ID cannot be null")
    private UUID questionId;

    @NotNull(message = "Ordinal number cannot be null")
    private Integer ordinalNumber;

    @NotNull(message = "content cannot be null")
    private String content;

    @NotNull(message = "Question type cannot be null")
    private QuestionType questionType;

    private List<@Valid CreateOptionRequest> options;

    private String blankAnswer;

    @NotNull(message = "Solution cannot be null")
    private String solution;

    private String terms;

    private String guide;
}
