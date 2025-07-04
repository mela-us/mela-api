package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDetailDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseResultDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseStatDetailDto;
import com.hcmus.mela.exercise.dto.response.GetAllExercisesResponse;
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
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
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

    private static final String EXERCISES_FOUND = "exercises_of_lecture_found_successful";
    private final GeneralMessageAccessor generalMessageAccessor;
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
            final String exercisesNotFoundMessage = generalMessageAccessor.getMessage(null, "exercises_not_found", lectureId);
            log.info(exercisesNotFoundMessage);
            return new GetExercisesInLectureResponse(exercisesNotFoundMessage, 0, new ArrayList<>());
        }

        List<ExerciseStatDetailDto> exerciseStatDetailDtoList = mapExercisesToStatDetails(
                exercises,
                userId,
                lectureId,
                lecture.getTopicId(),
                lecture.getLevelId()
        );

        final String exercisesSuccessMessage = generalMessageAccessor.getMessage(null, EXERCISES_FOUND, lectureId);
        log.info(exercisesSuccessMessage);

        return new GetExercisesInLectureResponse(
                exercisesSuccessMessage,
                exerciseStatDetailDtoList.size(),
                exerciseStatDetailDtoList);
    }

    @Override
    public GetAllExercisesResponse getAllExercises(ExerciseFilterStrategy strategy, UUID userId) {
        List<ExerciseDetailDto> exercises = strategy.getExercises(userId);
        if (exercises.isEmpty()) {
            return new GetAllExercisesResponse("No exercises found", Collections.emptyList());
        }
        return new GetAllExercisesResponse("Get exercises success", exercises);
    }


    @Override
    public GetExerciseInfoResponse getExerciseInfoByExerciseId(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId) {
        ExerciseDto exerciseDto = strategy.getExerciseById(userId, exerciseId);
        return new GetExerciseInfoResponse("Get exercise info successfully", exerciseDto);
    }

    @Override
    public List<ExerciseDto> getExercisesByLectureId(UUID lectureId) {
        List<Exercise> exercises = exerciseRepository.findAllByLectureId(lectureId);

        return exercises.stream()
                .map(ExerciseMapper.INSTANCE::exerciseToExerciseDto)
                .toList();
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
                        .totalCorrectAnswers((int) Math.round(numberOfQuestions * bestScore / 100))
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
