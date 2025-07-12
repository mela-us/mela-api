package com.hcmus.mela.history.dto.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsByTopic {

    private UUID topicId;

    private String name;

    private Double timeSpent;

    private Double averageTimeSpent;

    private Integer totalExercises;

    private Integer totalPassedExercises;

    private Integer totalAnswers;

    private Integer totalCorrectAnswers;

    private Double averageScore;
}