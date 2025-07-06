package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Lecture;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LectureCustomRepositoryImpl implements LectureCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Lecture> findLecturesByTopicAndLevel(UUID topicId, UUID levelId) {
        Criteria criteria = Criteria.where("topic_id").is(topicId)
                .and("level_id").is(levelId);
        Aggregation aggregation = buildLectureAggregation(criteria);
        return executeLectureAggregation(aggregation);
    }

    @Override
    public List<Lecture> findLecturesByKeyword(String keyword) {
        Criteria criteria = (keyword != null && !keyword.trim().isEmpty())
                ? Criteria.where("name").regex(keyword, "i")
                : new Criteria(); // match all
        Aggregation aggregation = buildLectureAggregation(criteria);
        return executeLectureAggregation(aggregation);
    }

    @Override
    public List<Lecture> findCompleteLecturesWithWrongExercises(UUID userId) {
        // Match by user_id and completed_at ≥ 3 days ago
        MatchOperation userAndDateMatch = Aggregation.match(
                new Criteria().andOperator(
                        Criteria.where("user_id").is(userId),
                        Criteria.where("completed_at")
                                .lte(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).minusDays(3))
                )
        );

        // Lookup exercise_histories with nested lookup to filter VERIFIED exercises
        AggregationOperation lookupExercise = context -> new Document("$lookup",
                new Document("from", "exercise_histories")
                        .append("let", new Document("lectureId", "$lecture_id").append("userId", "$user_id"))
                        .append("pipeline", Arrays.asList(
                                new Document("$match",
                                        new Document("$expr",
                                                new Document("$and", Arrays.asList(
                                                        new Document("$eq", Arrays.asList("$lecture_id", "$$lectureId")),
                                                        new Document("$eq", Arrays.asList("$user_id", "$$userId"))
                                                ))
                                        )
                                ),
                                // Lookup to join with exercises collection to get status
                                new Document("$lookup",
                                        new Document("from", "exercises")
                                                .append("localField", "exercise_id")
                                                .append("foreignField", "_id")
                                                .append("as", "exercise")
                                ),
                                new Document("$unwind", "$exercise"),
                                // status = "VERIFIED"
                                new Document("$match",
                                        new Document("exercise.status", "VERIFIED")
                                )
                        ))
                        .append("as", "exerciseResults")
        );

        // Unwind exerciseResults
        UnwindOperation unwindExercises = Aggregation.unwind("exerciseResults", true);

        AddFieldsOperation addDefaultScore = Aggregation.addFields()
                .addFieldWithValue("exerciseScore",
                        ConditionalOperators.ifNull("exerciseResults.score").then(0))
                .build();

        MatchOperation resultMatch = Aggregation.match(
                new Criteria().andOperator(
                        Criteria.where("progress").is(100),
                        Criteria.where("exerciseScore").lt(100)
                )
        );

        // Lookup lectures by lecture_id with status = "VERIFIED"
        AggregationOperation lookupLecture = context -> new Document("$lookup",
                new Document("from", "lectures")
                        .append("let", new Document("lectureId", "$lecture_id"))
                        .append("pipeline", Arrays.asList(
                                new Document("$match",
                                        new Document("$expr",
                                                new Document("$and", Arrays.asList(
                                                        new Document("$eq", Arrays.asList("$_id", "$$lectureId")),
                                                        new Document("$eq", Arrays.asList("$status", "VERIFIED"))
                                                ))
                                        )
                                )
                        ))
                        .append("as", "lecture")
        );

        // Unwind lectures (should be only one)
        UnwindOperation unwindLecture = Aggregation.unwind("lecture");

        // Replace root with lecture document
        GroupOperation groupAndGetMaxScore = Aggregation.group("lecture_id")
                .first("lecture").as("lecture")
                .first("completed_at").as("completedAt")
                .max("exerciseResults.score").as("maxScore");

        SortOperation sortOperation = Aggregation.sort(
                Sort.by(Sort.Order.asc("completedAt"), Sort.Order.asc("maxScore"))
        );

        ReplaceRootOperation replaceRootFinal = Aggregation.replaceRoot("lecture");

        Aggregation aggregation = Aggregation.newAggregation(
                userAndDateMatch,
                lookupExercise,
                unwindExercises,
                addDefaultScore,
                resultMatch,
                lookupLecture,
                unwindLecture,
                groupAndGetMaxScore,
                sortOperation,
                replaceRootFinal
        );

        AggregationResults<Lecture> results = mongoTemplate.aggregate(
                aggregation, "lecture_histories", Lecture.class
        );

        return results.getMappedResults();
    }

    private List<AggregationOperation> buildLectureJoinAggregation(Integer size) {
        return Arrays.asList(
                Aggregation.sort(Sort.Direction.DESC, "completed_at"),
                Aggregation.group("lecture_id")
                        .first("completed_at").as("completed_at"),
                Aggregation.sort(Sort.Direction.DESC, "completed_at"),
                Aggregation.lookup("lectures", "_id", "_id", "lecture"),
                Aggregation.unwind("lecture"),
                Aggregation.match(Criteria.where("lecture.status").is("VERIFIED")),
                Aggregation.limit(size),
                Aggregation.lookup("exercises", "_id", "lecture_id", "exercises"),
                Aggregation.unwind("exercises"),
                Aggregation.match(Criteria.where("exercises.status").is("VERIFIED")),
                Aggregation.group("lecture._id", "lecture.level_id", "lecture.topic_id", "lecture.ordinal_number", "lecture.name", "lecture.description", "completed_at")
                        .push("exercises").as("exercises"),
                Aggregation.project()
                        .and("lecture._id").as("lecture_id")
                        .and("lecture.level_id").as("level_id")
                        .and("lecture.topic_id").as("topic_id")
                        .and("lecture.ordinal_number").as("ordinal_number")
                        .and("lecture.name").as("name")
                        .and("lecture.description").as("description")
                        .and("completed_at").as("completed_at")
                        .and("exercises").size().as("total_exercises")
        );
    }

    private Aggregation buildLectureAggregation(Criteria matchCriteria) {
        return Aggregation.newAggregation(
                Aggregation.match(matchCriteria),
                Aggregation.match(Criteria.where("status").is("VERIFIED")),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description"),
                Aggregation.lookup("exercises", "_id", "lecture_id", "exercises"),
                Aggregation.unwind("exercises"),
                Aggregation.match(Criteria.where("exercises.status").is("VERIFIED")),
                Aggregation.group("_id", "level_id", "topic_id", "ordinal_number", "name", "description")
                        .push("exercises").as("exercises"),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description")
                        .and("exercises").size().as("total_exercises")
        );
    }

    private List<Lecture> executeLectureAggregation(Aggregation aggregation) {
        AggregationResults<Lecture> results = mongoTemplate.aggregate(aggregation, "lectures", Lecture.class);
        return results.getMappedResults();
    }
}
