package com.hcmus.mela.history.repository;

import com.hcmus.mela.history.model.BestResultByExercise;
import com.hcmus.mela.history.model.ExercisesCountByLecture;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExerciseHistoryCustomRepository {

    @Getter
    @Setter
    @Builder
    public class LevelTime {
        private UUID levelId;

        private double averageMinutes;
    }

    @Getter
    @Setter
    @Builder
    public class LevelTopicCount {
        private UUID levelId;

        private UUID topicId;

        private int count;
    }

    List<ExercisesCountByLecture> countTotalPassExerciseOfUser(UUID userId, Double passScore);

    List<BestResultByExercise> getBestExerciseResultsOfUserByLectureId(UUID userId, UUID lectureId);

    List<LevelTime> averageTimeByLevel(LocalDateTime start, LocalDateTime end);

    List<LevelTopicCount> countExerciseByLevelAndTopic(LocalDateTime start, LocalDateTime end);
}
