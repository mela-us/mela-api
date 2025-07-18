package com.hcmus.mela.history.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
@ToString
public class TestAnswer {

    @Field("question_id")
    private UUID questionId;

    @Field("is_correct")
    private Boolean isCorrect;

    @Field("blank_answer")
    private String blankAnswer;

    @Field("selected_option")
    private Integer selectedOption;

    @Field("images")
    private List<String> images;

    @Field("feedback")
    private String feedback;
}
