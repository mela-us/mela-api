package com.hcmus.mela.history.repository;

import com.hcmus.mela.history.model.BestResultByExercise;
import com.hcmus.mela.history.model.ExercisesCountByLecture;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExerciseHistoryCustomRepositoryImpl implements ExerciseHistoryCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ExercisesCountByLecture> countTotalPassExerciseOfUser(UUID userId, Double passScore) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("user_id").is(userId),
                        Criteria.where("score").gte(passScore)
                )),
                Aggregation.lookup("exercises", "exercise_id", "_id", "exercise_docs"),
                Aggregation.unwind("exercise_docs"),
                Aggregation.match(Criteria.where("exercise_docs.status").is("VERIFIED")),
                Aggregation.group("lecture_id").addToSet("exercise_id").as("exercises"),
                Aggregation.project("_id").and("exercises").size().as("total_exercises")
        );
        AggregationResults<ExercisesCountByLecture> result = mongoTemplate.aggregate(
                aggregation,
                "exercise_histories",
                ExercisesCountByLecture.class
        );
        return result.getMappedResults();
    }

    @Override
    public List<BestResultByExercise> getBestExerciseResultsOfUserByLectureId(UUID userId, UUID lectureId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("user_id").is(userId),
                        Criteria.where("lecture_id").is(lectureId)
                )),
                Aggregation.group("exercise_id").max("score").as("score"),
                Aggregation.project().and("_id").as("exercise_id")
                        .and("score").as("score")
        );
        AggregationResults<BestResultByExercise> result = mongoTemplate.aggregate(
                aggregation,
                "exercise_histories",
                BestResultByExercise.class
        );
        return result.getMappedResults();
    }

    @Override
    public List<LevelTime> averageTimeByLevel(LocalDateTime start, LocalDateTime end) {

        // Match stage
        MatchOperation match = Aggregation.match(Criteria.where("completed_at").gte(start).lte(end).ne(null));

        // Group stage
        GroupOperation group = Aggregation.group("level_id")
                .avg(
                        ArithmeticOperators.Divide.valueOf(
                                ArithmeticOperators.Subtract.valueOf("completed_at").subtract("started_at")
                        ).divideBy(60000)
                ).as("avgDuration");

        // Project stage
        ProjectionOperation project = Aggregation.project()
                .and("_id").as("levelId")
                .and("avgDuration").as("averageMinutes")
                .andExclude("_id");

        // Combine pipeline
        Aggregation aggregation = Aggregation.newAggregation(match, group, project);

        // Run aggregation
        List<Document> results = mongoTemplate.aggregate(aggregation, "exercise_histories", Document.class)
                .getMappedResults();


        return results.stream().map(doc -> LevelTime.builder()
                        .levelId(doc.get("levelId", UUID.class))
                        .averageMinutes(doc.getDouble("averageMinutes"))
                        .build())
                        .toList();
    }

    @Override
    public List<LevelTopicCount> countExerciseByLevelAndTopic(LocalDateTime start, LocalDateTime end) {
        // Match stage
        MatchOperation match = Aggregation.match(
                Criteria.where("completed_at").gte(start).lte(end).ne(null)
        );

        // Group stage
        GroupOperation group = Aggregation.group(
                        Fields.fields("level_id", "topic_id"))
                .count().as("count");

        // Project stage
        ProjectionOperation project = Aggregation.project()
                .and("_id.level_id").as("levelId")
                .and("_id.topic_id").as("topicId")
                .and("count").as("count")
                .andExclude("_id");

        // Combine pipeline
        Aggregation aggregation = Aggregation.newAggregation(match, group, project);

        // Run aggregation
        List<Document> results = mongoTemplate.aggregate(aggregation, "exercise_histories", Document.class)
                .getMappedResults();

        // Map to LevelTopicCount

        return results.stream()
                .map(doc -> LevelTopicCount.builder()
                        .levelId(doc.get("levelId", UUID.class))
                        .topicId(doc.get("topicId", UUID.class))
                        .count(doc.getInteger("count"))
                        .build())
                .toList();
    }
}
