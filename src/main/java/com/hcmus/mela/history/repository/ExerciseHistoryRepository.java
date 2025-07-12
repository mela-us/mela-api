package com.hcmus.mela.history.repository;

import com.hcmus.mela.history.model.ExerciseHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExerciseHistoryRepository extends MongoRepository<ExerciseHistory, UUID>, ExerciseHistoryCustomRepository {

    @Setter
    @Getter
    public class HourlyCount {
        private int hour;
        private int count;

    }

    List<ExerciseHistory> findAllByUserIdAndLevelId(UUID userId, UUID levelId);

    void deleteAllByUserId(UUID userId);

    int countByCompletedAtBetween(LocalDateTime start, LocalDateTime end);

    List<ExerciseHistory> findAllByCompletedAtBetween(LocalDateTime start, LocalDateTime end);

    @Aggregation(pipeline = {
            "{ $match: { $and: [ { 'completed_at': { $gte: ?0 } }, { 'completed_at': { $lte: ?1 } }, { 'completed_at': { $ne: null } } ] } }",
            "{ $group: { _id: { $hour: '$completed_at' }, count: { $sum: 1 } } }",
            "{ $project: { hour: '$_id', count: 1, _id: 0 } }"
    })
    List<HourlyCount> countByCompletedAtBetweenGroupByHour(LocalDateTime start, LocalDateTime end);

    Integer countAllByLectureId(UUID lectureId);

    Integer countAllByExerciseId(UUID exerciseId);

    List<ExerciseHistory> findAllByUserId(UUID userId);
}
