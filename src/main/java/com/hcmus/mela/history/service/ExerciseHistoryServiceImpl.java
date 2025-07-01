package com.hcmus.mela.history.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.service.ExerciseGradeService;
import com.hcmus.mela.exercise.service.ExerciseInfoService;
import com.hcmus.mela.history.dto.dto.AnswerResultDto;
import com.hcmus.mela.history.dto.dto.ExerciseHistoryDto;
import com.hcmus.mela.history.dto.request.ExerciseResultRequest;
import com.hcmus.mela.history.dto.response.ExerciseResultResponse;
import com.hcmus.mela.history.exception.HistoryException;
import com.hcmus.mela.history.mapper.ExerciseAnswerMapper;
import com.hcmus.mela.history.mapper.ExerciseHistoryMapper;
import com.hcmus.mela.history.model.BestResultByExercise;
import com.hcmus.mela.history.model.ExerciseAnswer;
import com.hcmus.mela.history.model.ExerciseHistory;
import com.hcmus.mela.history.model.ExercisesCountByLecture;
import com.hcmus.mela.history.repository.ExerciseHistoryRepository;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.service.LectureService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.ProjectConstants;
import com.hcmus.mela.skills.service.UserSkillService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ExerciseHistoryServiceImpl implements ExerciseHistoryService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;
    private final LectureService lectureService;
    private final ExerciseInfoService exerciseInfoService;
    private final ExerciseGradeService exerciseGradeService;
    private final UserSkillService userSkillService;

    @Override
    public ExerciseResultResponse getExerciseResultResponse(UUID userId, ExerciseResultRequest exerciseResultRequest) {
        if (!exerciseInfoService.checkExerciseStatus(exerciseResultRequest.getExerciseId(), ContentStatus.VERIFIED)) {
            throw new HistoryException("Exercise is not available for grading or does not exist.");
        }
        List<ExerciseAnswer> exerciseAnswerList = exerciseGradeService.gradeExercise(
                exerciseResultRequest.getExerciseId(),
                exerciseResultRequest.getAnswers()
        );
        saveExerciseHistory(
                userId,
                exerciseResultRequest.getStartedAt(),
                exerciseResultRequest.getCompletedAt(),
                exerciseResultRequest.getExerciseId(),
                exerciseAnswerList
        );
        log.info("Exercise result saved successfully for user: {}", userId);

        List<AnswerResultDto> answerResults = exerciseAnswerList.stream()
                .map(ExerciseAnswerMapper.INSTANCE::convertToAnswerResultDto)
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
        ExerciseDto exerciseInfo = exerciseInfoService.findByExerciseId(exerciseId);
        LectureDto lectureInfo = lectureService.getLectureById(exerciseInfo.getLectureId());

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

        userSkillService.updateUserSkill(userId,
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
        return exerciseHistories.stream().map(ExerciseHistoryMapper.INSTANCE::converToExerciseHistoryDto).toList();
    }
}
