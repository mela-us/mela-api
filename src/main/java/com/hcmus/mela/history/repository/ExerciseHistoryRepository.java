package com.hcmus.mela.history.repository;

import com.hcmus.mela.history.model.ExerciseHistory;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExerciseHistoryRepository extends MongoRepository<ExerciseHistory, UUID>, ExerciseHistoryCustomRepository {

    List<ExerciseHistory> findAllByUserIdAndLevelId(UUID userId, UUID levelId);

    void deleteAllByUserId(UUID userId);

    int countByCompletedAtBetween(LocalDateTime start, LocalDateTime end);

    List<ExerciseHistory> findAllByCompletedAtBetween(LocalDateTime start, LocalDateTime end);

    @Aggregation(pipeline = {
            "{ $match: { 'completedAt': { $gte: ?0, $lte: ?1 }, 'completedAt': { $ne: null } } }",
            "{ $group: { _id: { $hour: '$completedAt' }, count: { $sum: 1 } } }",
            "{ $project: { hour: '$_id', count: 1, _id: 0 } }"
    })
    List<HourlyCount> countByCompletedAtBetweenGroupByHour(LocalDateTime start, LocalDateTime end);

    interface HourlyCount {
        int getHour();

        int getCount();
    }

    @Aggregation(pipeline = {
            "{ $match: { 'completedAt': { $gte: ?0, $lte: ?1 }, 'startedAt': { $ne: null }, 'completedAt': { $ne: null } } }",
            "{ $group: { _id: '$levelId', avgDuration: { $avg: { $divide: [{ $subtract: ['$completedAt', '$startedAt'] }, 60000] } } } }",
            "{ $project: { levelId: '$_id', averageMinutes: '$avgDuration', _id: 0 } }"
    })
    List<LevelTime> averageTimeByLevel(LocalDateTime start, LocalDateTime end);

    interface LevelTime {
        UUID getLevelId();

        double getAverageMinutes();
    }

    @Aggregation(pipeline = {
            "{ $match: { 'completedAt': { $gte: ?0, $lte: ?1 }, 'completedAt': { $ne: null } } }",
            "{ $group: { _id: { levelId: '$levelId', topicId: '$topicId' }, count: { $sum: 1 } } }",
            "{ $project: { levelId: '$_id.levelId', topicId: '$_id.topicId', count: 1, _id: 0 } }"
    })
    List<LevelTopicCount> countByLevelIdAndTopicId(LocalDateTime start, LocalDateTime end);

    interface LevelTopicCount {
        UUID getLevelId();

        UUID getTopicId();

        int getCount();
    }

    Integer countAllByLectureId(UUID lectureId);

    Integer countAllByExerciseId(UUID exerciseId);

    List<ExerciseHistory> findAllByUserId(UUID userId);
}
