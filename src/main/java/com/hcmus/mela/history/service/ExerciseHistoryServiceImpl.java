package com.hcmus.mela.history.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.service.ExerciseGradeService;
import com.hcmus.mela.exercise.service.ExerciseInfoService;
import com.hcmus.mela.history.dto.dto.AnswerResultDto;
import com.hcmus.mela.history.dto.dto.ExerciseHistoryDto;
import com.hcmus.mela.history.dto.dto.StatsByTopic;
import com.hcmus.mela.history.dto.dto.UserExerciseStatsDto;
import com.hcmus.mela.history.dto.request.ExerciseResultRequest;
import com.hcmus.mela.history.dto.response.ExerciseResultResponse;
import com.hcmus.mela.history.dto.response.GetUserExerciseStatsResponse;
import com.hcmus.mela.history.exception.HistoryException;
import com.hcmus.mela.history.mapper.ExerciseAnswerMapper;
import com.hcmus.mela.history.mapper.ExerciseHistoryMapper;
import com.hcmus.mela.history.model.BestResultByExercise;
import com.hcmus.mela.history.model.ExerciseAnswer;
import com.hcmus.mela.history.model.ExerciseHistory;
import com.hcmus.mela.history.model.ExercisesCountByLecture;
import com.hcmus.mela.history.repository.ExerciseHistoryRepository;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.service.LectureInfoService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.ProjectConstants;
import com.hcmus.mela.skills.service.UserSkillService;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.service.TopicInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ExerciseHistoryServiceImpl implements ExerciseHistoryService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;
    private final LectureInfoService lectureInfoService;
    private final ExerciseInfoService exerciseInfoService;
    private final ExerciseGradeService exerciseGradeService;
    private final UserSkillService userSkillService;
    private final TopicInfoService topicInfoService;

    @Override
    public ExerciseResultResponse getExerciseResultResponse(UUID userId, ExerciseResultRequest request) {
        List<ExerciseAnswer> exerciseAnswerList = exerciseGradeService.gradeExercise(
                request.getExerciseId(),
                request.getAnswers()
        );
        saveExerciseHistory(
                userId,
                request.getStartedAt(),
                request.getCompletedAt(),
                request.getExerciseId(),
                exerciseAnswerList
        );
        log.info("Exercise result saved successfully for user: {}", userId);

        List<AnswerResultDto> answerResults = exerciseAnswerList.stream()
                .map(ExerciseAnswerMapper.INSTANCE::exerciseAnswerToAnswerResultDto)
                .toList();
        return new ExerciseResultResponse(
                "Exercise result submit successfully for user: " + userId,
                answerResults);
    }

    private void saveExerciseHistory(
            UUID userId,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            UUID exerciseId,
            List<ExerciseAnswer> answers) {
        ExerciseDto exerciseInfo = exerciseInfoService.findExerciseByExerciseIdAndStatus(exerciseId, ContentStatus.VERIFIED);
        if (exerciseInfo == null) {
            throw new HistoryException("Exercise not found or not verified");
        }
        LectureDto lectureInfo = lectureInfoService.findLectureByLectureId(exerciseInfo.getLectureId());
        if (startedAt == null) {
            startedAt = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }
        if (completedAt == null) {
            completedAt = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }
        Double score = answers.stream()
                .filter(ExerciseAnswer::getIsCorrect)
                .count() * 1.0 / answers.size() * 100;
        Long correctAnswers = answers.stream().filter(ExerciseAnswer::getIsCorrect).count();
        ExerciseHistory exerciseHistory = ExerciseHistory.builder()
                .id(UUID.randomUUID())
                .lectureId(lectureInfo.getLectureId())
                .userId(userId)
                .exerciseId(exerciseInfo.getExerciseId())
                .levelId(lectureInfo.getLevelId())
                .topicId(lectureInfo.getTopicId())
                .score(score)
                .startedAt(startedAt)
                .completedAt(completedAt)
                .answers(answers)
                .build();
        exerciseHistoryRepository.save(exerciseHistory);
        userSkillService.updateUserSkill(
                userId,
                lectureInfo.getLevelId(),
                lectureInfo.getTopicId(),
                correctAnswers.intValue(),
                answers.size() - correctAnswers.intValue());
    }

    @Override
    public Map<UUID, Integer> getPassedExerciseCountOfUser(UUID userId) {
        List<ExercisesCountByLecture> exercisesCountByLectureList = exerciseHistoryRepository
                .countTotalPassExerciseOfUser(userId, ProjectConstants.EXERCISE_PASS_SCORE);
        if (exercisesCountByLectureList == null || exercisesCountByLectureList.isEmpty()) {
            return Collections.emptyMap();
        }
        return exercisesCountByLectureList.stream()
                .collect(Collectors.toMap(
                        ExercisesCountByLecture::getLectureId,
                        ExercisesCountByLecture::getTotalExercises
                ));
    }

    @Override
    public Map<UUID, Double> getExerciseBestScoresOfUserByLecture(UUID userId, UUID lectureId) {
        List<BestResultByExercise> bestResultByExerciseList = exerciseHistoryRepository.getBestExerciseResultsOfUserByLectureId(userId, lectureId);
        Map<UUID, Double> bestScoreMap = new HashMap<>();
        for (BestResultByExercise bestResultByExercise : bestResultByExerciseList) {
            bestScoreMap.put(bestResultByExercise.getExerciseId(), bestResultByExercise.getScore());
        }
        return bestScoreMap;
    }

    @Override
    public List<ExerciseHistoryDto> getExerciseHistoryByUserAndLevel(UUID userId, UUID levelId) {
        List<ExerciseHistory> exerciseHistories = exerciseHistoryRepository.findAllByUserIdAndLevelId(userId, levelId);
        if (exerciseHistories == null || exerciseHistories.isEmpty()) {
            return new ArrayList<>();
        }
        return exerciseHistories.stream().map(ExerciseHistoryMapper.INSTANCE::exerciseHistoryToExerciseHistoryDto).toList();
    }

    @Override
    public void deleteAllExerciseHistoryByUserId(UUID userId) {
        exerciseHistoryRepository.deleteAllByUserId(userId);
    }

    @Override
    public Integer countDoneExercisesByLectureId(UUID lectureId) {
        return exerciseHistoryRepository.countAllByLectureId(lectureId);
    }

    @Override
    public Integer countDoneExerciseByExerciseId(UUID exerciseId) {
        return exerciseHistoryRepository.countAllByExerciseId(exerciseId);
    }

    @Override
    public GetUserExerciseStatsResponse getUserExerciseStats(UUID userId) {

        UserExerciseStatsDto userStats = new UserExerciseStatsDto();
        List<ExerciseHistory> exerciseHistories = exerciseHistoryRepository.findAllByUserId(userId);
        if (exerciseHistories.isEmpty()) {
            userStats.setAverageScore(0.0);
            userStats.setTotalPassedExercises(0);
            userStats.setTotalExercises(0);
            userStats.setTotalTimeSpent(0.0);
            userStats.setAverageTimeSpent(0.0);
            userStats.setTotalCorrectAnswers(0);
            userStats.setTotalAnswers(0);
            return new GetUserExerciseStatsResponse("No exercise history found for user", userStats);
        }
        List<TopicDto> topics = topicInfoService.findAllTopicsInStatus(ContentStatus.VERIFIED);
        Map<UUID, StatsByTopic> statsByTopicMap = topics.stream()
                .collect(Collectors.toMap(
                        TopicDto::getTopicId,
                        topic -> StatsByTopic.builder()
                                .topicId(topic.getTopicId())
                                .name(topic.getName())
                                .averageScore(0.0)
                                .timeSpent(0.0)
                                .averageTimeSpent(0.0)
                                .totalCorrectAnswers(0)
                                .totalAnswers(0)
                                .totalExercises(0)
                                .totalPassedExercises(0)
                                .build()
                ));
        int totalExercises = exerciseHistories.size();
        double totalScore = 0.0;
        int totalPassedExercises = 0;
        double totalTimeSpent = 0.0;
        int totalCorrectAnswers = 0;
        int totalAnswers = 0;
        for (ExerciseHistory history : exerciseHistories) {
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            double score = history.getScore();
            double timeSpent = history.getCompletedAt().atZone(zoneId).toEpochSecond()
                    - history.getStartedAt().atZone(zoneId).toEpochSecond();
            int passExerciseCount = history.getScore() >= ProjectConstants.EXERCISE_PASS_SCORE
                    ? 1 : 0;
            int correctAnswersCount = (int) history.getAnswers().stream()
                    .filter(ExerciseAnswer::getIsCorrect)
                    .count();
            int answerCount = history.getAnswers().size();

            totalPassedExercises += passExerciseCount;
            totalScore += score;
            totalCorrectAnswers += correctAnswersCount;
            totalAnswers += answerCount;
            totalTimeSpent += timeSpent / 60.0; // Convert seconds to minutes
            StatsByTopic stats = statsByTopicMap.get(history.getTopicId());
            if (stats != null) {
                stats.setAverageScore(stats.getAverageScore() + score);
                stats.setTimeSpent(stats.getTimeSpent() + timeSpent);
                stats.setTotalPassedExercises(stats.getTotalPassedExercises() + passExerciseCount);
                stats.setTotalExercises(stats.getTotalExercises() + 1);
                stats.setTotalAnswers(stats.getTotalAnswers() + answerCount);
                stats.setTotalCorrectAnswers(stats.getTotalCorrectAnswers() + correctAnswersCount);
            }
        }
        for (StatsByTopic stats : statsByTopicMap.values()) {
            if (stats.getTotalExercises() > 0) {
                stats.setAverageTimeSpent(stats.getTimeSpent() / stats.getTotalExercises());
                stats.setAverageScore(stats.getAverageScore() / stats.getTotalExercises());
            } else {
                stats.setAverageScore(0.0);
                stats.setAverageTimeSpent(0.0);
            }
        }
        userStats.setStatsByTopics(new ArrayList<>(statsByTopicMap.values()));
        userStats.setAverageScore(totalScore / totalExercises);
        userStats.setTotalPassedExercises(totalPassedExercises);
        userStats.setTotalExercises(totalExercises);
        userStats.setTotalTimeSpent(totalTimeSpent);
        userStats.setAverageTimeSpent(totalTimeSpent / totalExercises);
        userStats.setTotalCorrectAnswers(totalCorrectAnswers);
        userStats.setTotalAnswers(totalAnswers);
        return new GetUserExerciseStatsResponse("User exercise stats retrieved successfully", userStats);
    }
}
