package com.hcmus.mela.history.dto.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class UserExerciseStatsDto {

    Double averageScore;

    Integer totalPassedExercises;

    Integer totalExercises;

    Double totalTimeSpent;

    Double averageTimeSpent;

    Integer totalCorrectAnswers;

    Integer totalAnswers;

    List<StatsByTopic> statsByTopics;
}
