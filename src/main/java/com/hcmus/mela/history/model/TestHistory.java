package com.hcmus.mela.history.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "test_histories")
public class TestHistory {
    @Id
    @Field(name = "_id")
    private UUID id;

    @Field("user_id")
    private UUID userId;

    @Field("level_id")
    private UUID levelId;

    @Field("score")
    private Double score;

    @Field("started_at")
    private LocalDateTime startedAt;

    @Field("completed_at")
    private LocalDateTime completedAt;

    @Field("answers")
    private List<TestAnswer> answers;
}
