package com.hcmus.mela.test.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test_questions")
public class TestQuestion {

    @Field(name = "_id")
    private UUID testQuestionId;

    @Field(name = "questions")
    private List<Question> questions;

    @Field(name = "topic_id")
    private UUID topicId;

    @Field(name = "level_id")
    private UUID levelId;
}
