package com.hcmus.mela.skills.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_skills")
public class UserSkill {
    @Id
    @Field("_id")
    private UUID userSkillId;

    @Field("user_id")
    private UUID userId;

    @Field("level_id")
    private UUID levelId;

    @Field("topic_id")
    private UUID topicId;

    @Field("points")
    private Integer points;
}
