package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.*;
import com.hcmus.mela.exercise.dto.response.GetAllExercisesResponse;
import com.hcmus.mela.exercise.dto.response.GetExerciseContributionResponse;
import com.hcmus.mela.exercise.dto.response.GetExerciseInfoResponse;
import com.hcmus.mela.exercise.dto.response.GetExercisesInLectureResponse;
import com.hcmus.mela.exercise.exception.ExerciseException;
import com.hcmus.mela.exercise.mapper.ExerciseMapper;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.model.ExerciseStatus;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.exercise.strategy.ExerciseFilterStrategy;
import com.hcmus.mela.history.service.ExerciseHistoryService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.service.LectureInfoService;
import com.hcmus.mela.lecture.service.LectureStatusService;
import com.hcmus.mela.shared.async.AsyncCustomService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.ProjectConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseQueryServiceImpl implements ExerciseQueryService {

    private final ExerciseRepository exerciseRepository;
    private final LectureInfoService lectureInfoService;
    private final LectureStatusService lectureStatusService;
    private final ExerciseHistoryService exerciseHistoryService;
    private final AsyncCustomService asyncService;

    @Override
    public GetExercisesInLectureResponse getExercisesByLectureId(UUID lectureId, UUID userId) {
        if (!lectureStatusService.isLectureInStatus(lectureId, ContentStatus.VERIFIED)) {
            throw new ExerciseException("Lecture not found or not verified");
        }

        CompletableFuture<LectureDto> lectureFuture = asyncService.runAsync(
                () -> lectureInfoService.findLectureByLectureId(lectureId),
                null);
        CompletableFuture<List<Exercise>> exerciseFuture = asyncService.runAsync(
                () -> exerciseRepository.findAllByLectureIdAndStatus(lectureId, ContentStatus.VERIFIED),
                Collections.emptyList());

        CompletableFuture.allOf(lectureFuture, exerciseFuture).join();

        LectureDto lecture = lectureFuture.join();
        List<Exercise> exercises = exerciseFuture.join();

        if (exercises == null || exercises.isEmpty() || lecture == null) {
            return new GetExercisesInLectureResponse("No exercise found", 0, new ArrayList<>());
        }

        List<ExerciseStatDetailDto> exerciseStatDetailDtoList = mapExercisesToStatDetails(
                exercises,
                userId,
                lectureId,
                lecture.getTopicId(),
                lecture.getLevelId()
        );

        return new GetExercisesInLectureResponse(
                "Get exercises in lecture successful",
                exerciseStatDetailDtoList.size(),
                exerciseStatDetailDtoList);
    }

    @Override
    public GetAllExercisesResponse getAllExercises(ExerciseFilterStrategy strategy, UUID userId) {
        List<ExerciseDetailDto> exercises = strategy.getExercises(userId);
        if (exercises.isEmpty()) {
            return new GetAllExercisesResponse("No exercises found", Collections.emptyList());
        }
        return new GetAllExercisesResponse("Get exercises successfully", exercises);
    }


    @Override
    public GetExerciseInfoResponse getExerciseInfoByExerciseId(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId) {
        ExerciseDto exerciseDto = strategy.getExerciseById(userId, exerciseId);
        return new GetExerciseInfoResponse("Get exercise info successfully", exerciseDto);
    }

    @Override
    public GetExerciseContributionResponse getExerciseContribution(UUID userId) {
        ContributionDto contributionDto = new ContributionDto();
        List<Exercise> totalExercises = exerciseRepository.findAllByCreatedBy(userId);
        contributionDto.setTotalCreatedNumber(totalExercises.size());
        int totalVerifiedExerciseNumber = 0;
        int totalQuestionNumber = 0;
        int totalVerifiedQuestionNumber = 0;
        int totalAccessedNumber = 0;
        for (Exercise exercise : totalExercises) {
            if (exercise.getStatus() == ContentStatus.VERIFIED) {
                totalVerifiedExerciseNumber++;
                totalVerifiedQuestionNumber += Optional.ofNullable(exercise.getQuestions()).map(List::size).orElse(0);
            }
            totalQuestionNumber += Optional.ofNullable(exercise.getQuestions()).map(List::size).orElse(0);
            totalAccessedNumber += exerciseHistoryService.countDoneExerciseByExerciseId(exercise.getExerciseId());
        }
        contributionDto.setTotalQuestionCreatedNumber(totalQuestionNumber);
        contributionDto.setVerifiedNumber(totalVerifiedExerciseNumber);
        contributionDto.setTotalQuestionVerifiedNumber(totalVerifiedQuestionNumber);
        contributionDto.setAccessedContentNumber(totalAccessedNumber);
        return new GetExerciseContributionResponse(
                "Get exercise contribution successfully",
                contributionDto
        );
    }

    private List<ExerciseStatDetailDto> mapExercisesToStatDetails(
            List<Exercise> exercises,
            UUID userId,
            UUID lectureId,
            UUID topicId,
            UUID levelId
    ) {
        Map<UUID, Double> exerciseBestScoreMap = exerciseHistoryService.getExerciseBestScoresOfUserByLecture(userId, lectureId);

        List<ExerciseStatDetailDto> exerciseStatDetailDtoList = new ArrayList<>();
        for (Exercise exercise : exercises) {
            final UUID exerciseId = exercise.getExerciseId();
            final Integer numberOfQuestions = Optional.ofNullable(exercise.getQuestions()).map(List::size).orElse(0);

            ExerciseStatDetailDto exerciseStatDetailDto = ExerciseMapper.INSTANCE.exerciseToExerciseStatDetailDto(exercise);
            exerciseStatDetailDto.setTopicId(topicId);
            exerciseStatDetailDto.setLevelId(levelId);
            exerciseStatDetailDto.setTotalQuestions(numberOfQuestions);

            if (exerciseBestScoreMap.containsKey(exerciseId)) {
                double bestScore = exerciseBestScoreMap.get(exerciseId);
                ExerciseResultDto exerciseResultDto = ExerciseResultDto.builder()
                        .status(bestScore >= ProjectConstants.EXERCISE_PASS_SCORE ? ExerciseStatus.PASS : ExerciseStatus.IN_PROGRESS)
                        .totalAnswers(numberOfQuestions)
                        .totalCorrectAnswers((int) Math.floor(numberOfQuestions * bestScore / 100))
                        .build();
                exerciseStatDetailDto.setBestResult(exerciseResultDto);
            } else {
                exerciseStatDetailDto.setBestResult(null);
            }

            exerciseStatDetailDtoList.add(exerciseStatDetailDto);
        }
        return exerciseStatDetailDtoList;
    }
}
