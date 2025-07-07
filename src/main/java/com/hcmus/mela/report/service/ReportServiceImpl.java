package com.hcmus.mela.report.service;

import com.hcmus.mela.history.model.ExerciseHistory;
import com.hcmus.mela.history.repository.ExerciseHistoryRepository;
import com.hcmus.mela.history.repository.TestHistoryRepository;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.repository.LevelRepository;
import com.hcmus.mela.report.dto.dto.StatDto;
import com.hcmus.mela.report.dto.response.*;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import com.hcmus.mela.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserRepository userRepository;
    private final ExerciseHistoryRepository exerciseHistoryRepository;
    private final TestHistoryRepository testHistoryRepository;
    private final LevelRepository levelRepository;
    private final TopicRepository topicRepository;

    @Override
    public NewUsersStatResponse getNewUsersStat() {
        // Get current time in Asia/Ho_Chi_Minh timezone
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime startOfCurrentMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime startOfNextMonth = startOfCurrentMonth.plusMonths(1);

        // Get time range for the previous month
        ZonedDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);

        // Convert to Date for MongoDB query
        Date currentMonthStart = Date.from(startOfCurrentMonth.toInstant());
        Date nextMonthStart = Date.from(startOfNextMonth.toInstant());
        Date previousMonthStart = Date.from(startOfPreviousMonth.toInstant());
        Date previousMonthEnd = Date.from(startOfCurrentMonth.toInstant());

        // Count new users in the current month
        int currentCount = userRepository.countByCreatedAtBetween(currentMonthStart, nextMonthStart);

        // Count new users in the previous month
        int previousCount = userRepository.countByCreatedAtBetween(previousMonthStart, previousMonthEnd);

        // Calculate percentage change
        double percentChange = previousCount == 0 ? 0.0 : ((double) (currentCount - previousCount) / previousCount) * 100;

        // Create StatDto with rounded percentage change
        StatDto statDto = new StatDto(currentCount, previousCount, Math.round(percentChange * 10.0) / 10.0);

        // Return response
        return new NewUsersStatResponse("Success getting new users stats", statDto);
    }

    @Override
    public CompletedTestStatResponse getCompletedTestsStat() {
        // Get current time in Asia/Ho_Chi_Minh timezone
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime startOfCurrentMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime startOfNextMonth = startOfCurrentMonth.plusMonths(1);

        // Get time range for the previous month
        ZonedDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);

        // Convert to LocalDateTime for MongoDB query
        LocalDateTime currentMonthStart = startOfCurrentMonth.toLocalDateTime();
        LocalDateTime nextMonthStart = startOfNextMonth.toLocalDateTime();
        LocalDateTime previousMonthStart = startOfPreviousMonth.toLocalDateTime();
        LocalDateTime previousMonthEnd = startOfCurrentMonth.toLocalDateTime();

        // Count completed tests in the current month
        int currentCount = testHistoryRepository.countByCompletedAtBetween(currentMonthStart, nextMonthStart);

        // Count completed tests in the previous month
        int previousCount = testHistoryRepository.countByCompletedAtBetween(previousMonthStart, previousMonthEnd);

        // Calculate percentage change
        double percentChange = previousCount == 0 ? 0.0 : ((double) (currentCount - previousCount) / previousCount) * 100;

        // Create StatDto with rounded percentage change
        StatDto statDto = new StatDto(currentCount, previousCount, Math.round(percentChange * 10.0) / 10.0);

        // Return response
        return new CompletedTestStatResponse("Success", statDto);

    }

    @Override
    public CompletedExercisesStatResponse getCompletedExercisesStat() {
        // Get current time in Asia/Ho_Chi_Minh timezone
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime startOfCurrentMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime startOfNextMonth = startOfCurrentMonth.plusMonths(1);

        // Get time range for the previous month
        ZonedDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);

        // Convert to LocalDateTime for MongoDB query
        LocalDateTime currentMonthStart = startOfCurrentMonth.toLocalDateTime();
        LocalDateTime nextMonthStart = startOfNextMonth.toLocalDateTime();
        LocalDateTime previousMonthStart = startOfPreviousMonth.toLocalDateTime();
        LocalDateTime previousMonthEnd = startOfCurrentMonth.toLocalDateTime();

        // Count completed exercises in the current month
        int currentCount = exerciseHistoryRepository.countByCompletedAtBetween(currentMonthStart, nextMonthStart);

        // Count completed exercises in the previous month
        int previousCount = exerciseHistoryRepository.countByCompletedAtBetween(previousMonthStart, previousMonthEnd);

        // Calculate percentage change
        double percentChange = previousCount == 0 ? 0.0 : ((double) (currentCount - previousCount) / previousCount) * 100;

        // Create StatDto with rounded percentage change
        StatDto statDto = new StatDto(currentCount, previousCount, Math.round(percentChange * 10.0) / 10.0);

        // Return response
        return new CompletedExercisesStatResponse("Success getting completed exercises stats", statDto);
    }

    @Override
    public ExerciseAverageTimeStatResponse getExerciseAverageTimeStat() {
        // Get current time in Asia/Ho_Chi_Minh timezone
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime startOfCurrentMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime startOfNextMonth = startOfCurrentMonth.plusMonths(1);

        // Get time range for the previous month
        ZonedDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);

        // Convert to LocalDateTime for MongoDB query
        LocalDateTime currentMonthStart = startOfCurrentMonth.toLocalDateTime();
        LocalDateTime nextMonthStart = startOfNextMonth.toLocalDateTime();
        LocalDateTime previousMonthStart = startOfPreviousMonth.toLocalDateTime();
        LocalDateTime previousMonthEnd = startOfCurrentMonth.toLocalDateTime();

        // Get exercise history records for the current month
        List<ExerciseHistory> currentMonthRecords = exerciseHistoryRepository.findAllByCompletedAtBetween(currentMonthStart, nextMonthStart);

        // Calculate average time for the current month (in minutes)
        double currentAvgTime = currentMonthRecords.isEmpty() ? 0.0 :
                currentMonthRecords.stream()
                        .filter(record -> record.getStartedAt() != null && record.getCompletedAt() != null)
                        .mapToDouble(record -> Duration.between(record.getStartedAt(), record.getCompletedAt()).toMinutes())
                        .average()
                        .orElse(0.0);

        // Get exercise history records for the previous month
        List<ExerciseHistory> previousMonthRecords = exerciseHistoryRepository.findAllByCompletedAtBetween(previousMonthStart, previousMonthEnd);

        // Calculate average time for the previous month (in minutes)
        double previousAvgTime = previousMonthRecords.isEmpty() ? 0.0 :
                previousMonthRecords.stream()
                        .filter(record -> record.getStartedAt() != null && record.getCompletedAt() != null)
                        .mapToDouble(record -> Duration.between(record.getStartedAt(), record.getCompletedAt()).toMinutes())
                        .average()
                        .orElse(0.0);

        // Calculate percentage change
        double percentChange = previousAvgTime == 0.0 ? 0.0 : ((currentAvgTime - previousAvgTime) / previousAvgTime) * 100;

        // Create StatDto with rounded values (cast to int as per JSON structure)
        StatDto statDto = new StatDto((int) Math.round(currentAvgTime), (int) Math.round(previousAvgTime), Math.round(percentChange * 10.0) / 10.0);

        // Return response
        return new ExerciseAverageTimeStatResponse("Success getting exercise average time stats", statDto);
    }

    @Override
    public HourlyExerciseDataResponse getHourlyExerciseData() {
        // Get current time in Asia/Ho_Chi_Minh timezone
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime startOfCurrentMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime startOfNextMonth = startOfCurrentMonth.plusMonths(1);

        // Convert to LocalDateTime for MongoDB query
        LocalDateTime currentMonthStart = startOfCurrentMonth.toLocalDateTime();
        LocalDateTime nextMonthStart = startOfNextMonth.toLocalDateTime();

        // Get hourly exercise completion counts
        List<ExerciseHistoryRepository.HourlyCount> hourlyCounts = exerciseHistoryRepository.countByCompletedAtBetweenGroupByHour(currentMonthStart, nextMonthStart);

        // Initialize list for all 24 hours (0-23)
        List<HourlyExerciseDataResponse.HourlyActivity> hourlyActivities = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            hourlyActivities.add(new HourlyExerciseDataResponse.HourlyActivity(String.valueOf(hour), 0));
        }

        // Update counts for hours with data
        for (ExerciseHistoryRepository.HourlyCount count : hourlyCounts) {
            int hour = count.getHour();
            if (hour >= 0 && hour < 24) {
                hourlyActivities.set(hour, new HourlyExerciseDataResponse.HourlyActivity(String.valueOf(hour), count.getCount()));
            }
        }

        // Return response
        return new HourlyExerciseDataResponse("Success", hourlyActivities);
    }

    @Override
    public UserGrowthDataResponse getUserGrowthData() {
        // Get current time in Asia/Ho_Chi_Minh timezone (July 2025)
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime startOfCurrentMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Get the start of 12 months ago (June 2024)
        ZonedDateTime startOfPeriod = startOfCurrentMonth.minusMonths(12);

        // Convert to LocalDateTime for MongoDB query
        LocalDateTime startDate = startOfPeriod.toLocalDateTime();
        LocalDateTime endDate = startOfCurrentMonth.plusMonths(1).toLocalDateTime();

        // Get monthly user counts
        List<UserRepository.MonthlyCount> monthlyCounts = userRepository.countByMonthInPeriod(
                java.util.Date.from(startDate.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant()),
                java.util.Date.from(endDate.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant())
        );

        // Initialize list for all 12 months
        List<UserGrowthDataResponse.UserGrowth> userGrowthList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 11; i >= 0; i--) {
            ZonedDateTime month = startOfCurrentMonth.minusMonths(i);
            String monthStr = month.format(formatter);
            userGrowthList.add(new UserGrowthDataResponse.UserGrowth(monthStr, 0));
        }

        // Update counts for months with data
        for (UserRepository.MonthlyCount count : monthlyCounts) {
            String monthStr = String.format("%d-%02d", count.getYear(), count.getMonth());
            int index = 11 - (startOfCurrentMonth.getYear() - count.getYear()) * 12 - (startOfCurrentMonth.getMonthValue() - count.getMonth());
            if (index >= 0 && index < 12) {
                userGrowthList.set(index, new UserGrowthDataResponse.UserGrowth(monthStr, count.getCount()));
            }
        }

        // Return response
        return new UserGrowthDataResponse("Success", userGrowthList);
    }

    /**
     * Retrieves average time taken to complete exercises per level for the current month,
     * based on the difference between startedAt and completedAt in the exercise_histories collection.
     *
     * @return AverageTimeByLevelDataResponse containing the average time data per level.
     */
    @Override
    public AverageTimeByLevelDataResponse getAverageTimeByLevelData() {
        // Get current time in Asia/Ho_Chi_Minh timezone (July 2025)
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime startOfCurrentMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime startOfNextMonth = startOfCurrentMonth.plusMonths(1);

        // Convert to LocalDateTime for MongoDB query
        LocalDateTime currentMonthStart = startOfCurrentMonth.toLocalDateTime();
        LocalDateTime nextMonthStart = startOfNextMonth.toLocalDateTime();

        // Get average time per level
        List<ExerciseHistoryRepository.LevelTime> levelTimes = exerciseHistoryRepository.averageTimeByLevel(currentMonthStart, nextMonthStart);

        // Get all levels to ensure every level is included
        List<Level> allLevels = levelRepository.findAll();
        Map<UUID, String> levelIdToName = allLevels.stream()
                .collect(Collectors.toMap(Level::getLevelId, Level::getName));

        // Initialize response list
        List<AverageTimeByLevelDataResponse.LevelTime> levelTimeList = new ArrayList<>();

        // Process aggregation results
        for (Level level : allLevels) {
            UUID levelId = level.getLevelId();
            String levelName = level.getName();
            int averageMinutes = 0;

            Optional<ExerciseHistoryRepository.LevelTime> levelTime = levelTimes.stream()
                    .filter(lt -> lt.getLevelId().equals(levelId))
                    .findFirst();

            if (levelTime.isPresent()) {
                averageMinutes = (int) Math.round(levelTime.get().getAverageMinutes());
            }

            levelTimeList.add(new AverageTimeByLevelDataResponse.LevelTime(levelName, averageMinutes));
        }

        // Sort by level name for consistent output
        levelTimeList.sort(Comparator.comparing(AverageTimeByLevelDataResponse.LevelTime::getLevel));

        // Return response
        return new AverageTimeByLevelDataResponse("Success", levelTimeList);
    }

    /**
     * Retrieves a heatmap of exercise completion counts by level and topic for the current month,
     * based on the exercise_histories collection.
     *
     * @return TopicLevelHeatmapDataResponse containing the heatmap data.
     */
    @Override
    public TopicLevelHeatmapDataResponse getTopicLevelHeatmapData() {
        // Get current time in Asia/Ho_Chi_Minh timezone (July 2025)
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime startOfCurrentMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime startOfNextMonth = startOfCurrentMonth.plusMonths(1);

        // Convert to LocalDateTime for MongoDB query
        LocalDateTime currentMonthStart = startOfCurrentMonth.toLocalDateTime();
        LocalDateTime nextMonthStart = startOfNextMonth.toLocalDateTime();

        // Get completion counts by level and topic
        List<ExerciseHistoryRepository.LevelTopicCount> levelTopicCounts = exerciseHistoryRepository.countByLevelIdAndTopicId(currentMonthStart, nextMonthStart);

        // Get all levels and topics
        List<Level> allLevels = levelRepository.findAll();
        List<Topic> allTopics = topicRepository.findAll();

        Map<UUID, String> levelIdToName = allLevels.stream()
                .collect(Collectors.toMap(Level::getLevelId, Level::getName));
        Map<UUID, String> topicIdToName = allTopics.stream()
                .collect(Collectors.toMap(Topic::getTopicId, Topic::getName));

        // Initialize response list
        List<TopicLevelHeatmapDataResponse.LevelTopics> levelTopicsList = new ArrayList<>();

        // Group counts by level
        Map<UUID, List<ExerciseHistoryRepository.LevelTopicCount>> countsByLevel = levelTopicCounts.stream()
                .collect(Collectors.groupingBy(ExerciseHistoryRepository.LevelTopicCount::getLevelId));

        // Process each level
        for (Level level : allLevels) {
            UUID levelId = level.getLevelId();
            String levelName = level.getName();
            List<TopicLevelHeatmapDataResponse.LevelTopics.Topic> topicList = new ArrayList<>();

            // Initialize topics with zero counts
            for (Topic topic : allTopics) {
                topicList.add(new TopicLevelHeatmapDataResponse.LevelTopics.Topic(topic.getName(), 0));
            }

            // Update counts for topics with data
            List<ExerciseHistoryRepository.LevelTopicCount> levelCounts = countsByLevel.getOrDefault(levelId, Collections.emptyList());
            for (ExerciseHistoryRepository.LevelTopicCount count : levelCounts) {
                String topicName = topicIdToName.get(count.getTopicId());
                int index = topicList.indexOf(topicList.stream()
                        .filter(t -> t.getName().equals(topicName))
                        .findFirst()
                        .orElse(null));
                if (index >= 0) {
                    topicList.set(index, new TopicLevelHeatmapDataResponse.LevelTopics.Topic(topicName, count.getCount()));
                }
            }

            // Sort topics by name for consistent output
            topicList.sort(Comparator.comparing(TopicLevelHeatmapDataResponse.LevelTopics.Topic::getName));

            levelTopicsList.add(new TopicLevelHeatmapDataResponse.LevelTopics(levelName, topicList));
        }

        // Sort levels by name for consistent output
        levelTopicsList.sort(Comparator.comparing(TopicLevelHeatmapDataResponse.LevelTopics::getLevel));

        // Return response
        return new TopicLevelHeatmapDataResponse("Success", levelTopicsList);
    }
}