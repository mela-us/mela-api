package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.*;
import com.hcmus.mela.exercise.dto.request.CreateExerciseRequest;
import com.hcmus.mela.exercise.dto.request.ExerciseRequest;
import com.hcmus.mela.exercise.dto.request.UpdateExerciseRequest;
import com.hcmus.mela.exercise.dto.response.*;
import com.hcmus.mela.exercise.exception.ExerciseException;
import com.hcmus.mela.exercise.mapper.ExerciseMapper;
import com.hcmus.mela.exercise.mapper.ExerciseStatDetailMapper;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.model.ExerciseStatus;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.exercise.strategy.ExerciseFilterStrategy;
import com.hcmus.mela.history.service.ExerciseHistoryService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.service.LectureService;
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
public class ExerciseServiceImpl implements ExerciseService {

    private static final String EXERCISE_FOUND = "exercise_found_successful";

    private static final String EXERCISES_FOUND = "exercises_of_lecture_found_successful";

    private final GeneralMessageAccessor generalMessageAccessor;

    private final ExerciseRepository exerciseRepository;

    private final ExerciseValidationService exerciseValidationService;

    private final LectureService lectureService;

    private final ExerciseInfoService exerciseInfoService;

    private final ExerciseHistoryService exerciseHistoryService;

    private final AsyncCustomService asyncService;

    @Override
    public ExerciseResponse getAllExercisesInLecture(ExerciseRequest exerciseRequest) {
        exerciseValidationService.validateLecture(exerciseRequest);

        UUID lectureId = exerciseRequest.getLectureId();
        CompletableFuture<LectureDto> lectureFuture = asyncService.runAsync(
                () -> lectureService.getLectureByIdAndStatus(lectureId, ContentStatus.VERIFIED),
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
            return new ExerciseResponse(exercisesNotFoundMessage, 0, new ArrayList<>());
        }

        List<ExerciseStatDetailDto> exerciseStatDetailDtoList = mapExercisesToStatDetails(
                exercises,
                exerciseRequest.getUserId(),
                lectureId,
                lecture.getTopicId(),
                lecture.getLevelId()
        );

        final String exercisesSuccessMessage = generalMessageAccessor.getMessage(null, EXERCISES_FOUND, lectureId);
        log.info(exercisesSuccessMessage);

        return new ExerciseResponse(
                exercisesSuccessMessage,
                exerciseStatDetailDtoList.size(),
                exerciseStatDetailDtoList);
    }

    @Override
    public GetExercisesResponse getExercisesResponse(ExerciseFilterStrategy strategy, UUID userId) {
        List<ExerciseDetailDto> exercises = strategy.getExercises(userId);
        if (exercises.isEmpty()) {
            return new GetExercisesResponse("No exercises found", Collections.emptyList());
        }
        return new GetExercisesResponse("Get exercises success", exercises);
    }

    @Override
    public CreateExerciseResponse getCreateExerciseResponse(ExerciseFilterStrategy strategy, UUID userId, CreateExerciseRequest request) {
        Exercise exercise = ExerciseMapper.INSTANCE.createExerciseRequestToExercise(request);
        exercise.setExerciseId(UUID.randomUUID());
        exercise.setStatus(ContentStatus.PENDING);
        exercise.setCreatedBy(userId);
        ExerciseDto exerciseDto = strategy.createExercise(userId, exercise);

        return new CreateExerciseResponse(
                "Create exercise successfully",
                exerciseDto
        );
    }

    @Override
    public void updateExercise(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId, UpdateExerciseRequest request) {
        strategy.updateExercise(userId, exerciseId, request);
    }

    @Override
    public GetExerciseInfoResponse getExerciseInfoResponse(ExerciseFilterStrategy strategy, UUID userId, UUID exerciseId) {
        ExerciseDto exerciseDto = strategy.getExerciseById(userId, exerciseId);
        return new GetExerciseInfoResponse("Get exercise info successfully", exerciseDto);
    }

    @Override
    public void denyExercise(UUID exerciseId, String reason) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found"));
        if (exercise.getStatus() == ContentStatus.VERIFIED || exercise.getStatus() == ContentStatus.DELETED) {
            throw new ExerciseException("Exercise cannot be denied");
        }
        exercise.setRejectedReason(reason);
        exercise.setStatus(ContentStatus.DENIED);
        exerciseRepository.save(exercise);
    }

    @Override
    public void approveExercise(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException("Exercise not found"));
        if (exercise.getStatus() == ContentStatus.DELETED) {
            throw new ExerciseException("Exercise cannot be approved");
        }
        if (!lectureService.checkLectureStatus(exercise.getLectureId(), ContentStatus.VERIFIED)) {
            throw new ExerciseException("Lecture of exercise must be verified before approving exercise");
        }
        exercise.setRejectedReason(null);
        exercise.setStatus(ContentStatus.VERIFIED);
        exerciseRepository.save(exercise);
    }

    @Override
    public boolean checkExerciseStatus(UUID exerciseId, ContentStatus status) {
        if (exerciseId == null || status == null) {
            return false;
        }
        Exercise exercise = exerciseRepository.findById(exerciseId).orElse(null);
        return exercise != null && exercise.getStatus() == status;
    }

    @Override
    public void deleteExercise(ExerciseFilterStrategy strategy, UUID exerciseId, UUID userId) {
        strategy.deleteExercise(userId, exerciseId);
    }

    @Override
    public QuestionResponse getListQuestionsOfExercise(ExerciseRequest exerciseRequest) {
        exerciseValidationService.validateExercise(exerciseRequest);

        final UUID exerciseId = exerciseRequest.getExerciseId();

        ExerciseDto exerciseDto = exerciseInfoService.findByExerciseIdAndStatus(exerciseId, ContentStatus.VERIFIED);
        List<QuestionDto> questionDtoList = new ArrayList<>();
        if (exerciseDto != null) {
            questionDtoList = exerciseDto.getQuestions();
        }

        final String exerciseSuccessMessage = generalMessageAccessor.getMessage(null, EXERCISE_FOUND, exerciseId);
        log.info(exerciseSuccessMessage);

        return new QuestionResponse(exerciseSuccessMessage, questionDtoList.size(), questionDtoList);
    }

    @Override
    public List<ExerciseDto> getListOfExercisesInLecture(UUID lectureId) {
        List<Exercise> exercises = exerciseRepository.findAllByLectureId(lectureId);

        return exercises.stream()
                .map(ExerciseMapper.INSTANCE::converToExerciseDto)
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

            ExerciseStatDetailDto exerciseStatDetailDto = ExerciseStatDetailMapper.INSTANCE.convertToExerciseStatDetailDto(exercise);
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

    @Override
    public Exercise findByQuestionId(UUID questionId) {
        return exerciseRepository.findByQuestionsQuestionId(questionId);
    }

    @Override
    public void updateQuestionHint(Exercise exercise) {
        Exercise result = exerciseRepository.updateQuestionHint(exercise);
        if (result == null) {
            log.warn("Exercise {} not found", exercise.getExerciseId());
            return;
        }
        log.debug("Exercise {} updated successfully", result.getExerciseId());
    }
}
