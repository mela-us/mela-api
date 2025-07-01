package com.hcmus.mela.exercise.dto.request;

import com.hcmus.mela.exercise.model.Option;
import com.hcmus.mela.exercise.model.QuestionType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionRequest {

    private UUID questionId;

    private Integer ordinalNumber;

    private String content;

    private QuestionType questionType;

    private List<Option> options;

    private String blankAnswer;

    private String solution;

    private String terms;

    private String guide;
}
