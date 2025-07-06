package com.hcmus.mela.review.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.service.ExerciseInfoService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.lecture.service.LectureInfoService;
import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.service.LevelInfoService;
import com.hcmus.mela.review.dto.dto.ExerciseReferenceDto;
import com.hcmus.mela.review.dto.dto.ReviewDto;
import com.hcmus.mela.review.dto.dto.SectionReferenceDto;
import com.hcmus.mela.review.dto.request.UpdateReviewRequest;
import com.hcmus.mela.review.dto.response.GetReviewsResponse;
import com.hcmus.mela.review.dto.response.UpdateReviewResponse;
import com.hcmus.mela.review.exception.ReviewNotFoundException;
import com.hcmus.mela.review.mapper.ReviewMapper;
import com.hcmus.mela.review.model.ExerciseReference;
import com.hcmus.mela.review.model.Review;
import com.hcmus.mela.review.model.ReviewType;
import com.hcmus.mela.review.model.SectionReference;
import com.hcmus.mela.review.repository.ReviewRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.service.TopicInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final String REVIEWS_FOUND = "reviews_found_successful";
    private final String UPDATE_REVIEW_SUCCESS = "update_review_successful";

    private final GeneralMessageAccessor generalMessageAccessor;
    private final ReviewRepository reviewRepository;
    private final ExerciseInfoService exerciseInfoService;
    private final TopicInfoService topicInfoService;
    private final LevelInfoService levelInfoService;
    private final LectureInfoService lectureInfoService;

    @Override
    public GetReviewsResponse getReviews(UUID userId) {
        ZoneId zoneVN = ZoneId.of("Asia/Ho_Chi_Minh");

        ZonedDateTime startOfDayVN = ZonedDateTime.now(zoneVN).toLocalDate().atStartOfDay(zoneVN);
        ZonedDateTime endOfDayVN = startOfDayVN.plusDays(1);

        Date startOfDay = Date.from(startOfDayVN.toInstant());
        Date endOfDay = Date.from(endOfDayVN.toInstant());

        List<Review> reviews = reviewRepository.findAllByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);

        if (reviews == null || reviews.isEmpty()) {
            reviews = createReview(userId, startOfDay);
        }

        List<ReviewDto> reviewDtos = reviews.stream()
                .map(ReviewMapper.INSTANCE::reviewToReviewDto)
                .sorted(Comparator.comparing(ReviewDto::getOrdinalNumber))
                .toList();

        for (ReviewDto reviewDto : reviewDtos) {
            List<ExerciseReferenceDto> exerciseReferenceDtos = new ArrayList<>();
            for (ExerciseReferenceDto exerciseReferenceDto : reviewDto.getExerciseList()) {
                ExerciseDto exerciseDto = exerciseInfoService.findExerciseByExerciseIdAndStatus(
                        exerciseReferenceDto.getExerciseId(), ContentStatus.VERIFIED);
                if (exerciseDto == null) {
                    continue;
                }
                LectureDto lectureDto = lectureInfoService.findLectureByLectureId(exerciseDto.getLectureId());
                TopicDto topicDto = topicInfoService.findTopicByTopicId(lectureDto.getTopicId());
                LevelDto levelDto = levelInfoService.findLevelByLevelId(lectureDto.getLevelId());

                exerciseReferenceDto.setLectureTitle(lectureDto.getName());
                exerciseReferenceDto.setTopicTitle(topicDto.getName());
                exerciseReferenceDto.setLevelTitle(levelDto.getName());
                exerciseReferenceDtos.add(exerciseReferenceDto);
            }
            reviewDto.setExerciseList(exerciseReferenceDtos);

            List<SectionReferenceDto> sectionReferenceDtos = new ArrayList<>();
            for (SectionReferenceDto sectionReferenceDto : reviewDto.getSectionList()) {
                LectureDto lectureDto = lectureInfoService.findLectureByLectureIdAndStatus(
                        sectionReferenceDto.getLectureId(), ContentStatus.VERIFIED);
                if (lectureDto == null) {
                    continue;
                }
                TopicDto topicDto = topicInfoService.findTopicByTopicId(lectureDto.getTopicId());
                LevelDto levelDto = levelInfoService.findLevelByLevelId(lectureDto.getLevelId());

                sectionReferenceDto.setLectureTitle(lectureDto.getName());
                sectionReferenceDto.setTopicTitle(topicDto.getName());
                sectionReferenceDto.setLevelTitle(levelDto.getName());
                String sectionUrl = lectureDto.getSections()
                        .stream()
                        .filter(s -> s.getOrdinalNumber().equals(sectionReferenceDto.getOrdinalNumber()))
                        .findFirst()
                        .map(SectionDto::getUrl)
                        .orElse(null);
                sectionReferenceDto.setSectionUrl(sectionUrl);
                sectionReferenceDtos.add(sectionReferenceDto);
            }
            reviewDto.setSectionList(sectionReferenceDtos);
        }

        final String getReviewsSuccessMessage = generalMessageAccessor.getMessage(null, REVIEWS_FOUND, userId);
        return new GetReviewsResponse(getReviewsSuccessMessage, reviewDtos);
    }

    @Override
    public UpdateReviewResponse updateReview(UUID userId, UUID reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ReviewNotFoundException("Review of user " + userId + " not found with id " + reviewId));

        if (request.getType() == ReviewType.EXERCISE) {
            for (ExerciseReference exerciseReference : review.getExerciseList()) {
                if (exerciseReference.getExerciseId().equals(request.getItemId())
                        && exerciseReference.getOrdinalNumber().equals(request.getOrdinalNumber())) {
                    exerciseReference.setIsDone(request.getIsDone());
                    break;
                }
            }
        } else {
            for (SectionReference sectionReference : review.getSectionList()) {
                if (sectionReference.getLectureId().equals(request.getItemId())
                        && sectionReference.getOrdinalNumber().equals(request.getOrdinalNumber())) {
                    sectionReference.setIsDone(request.getIsDone());
                    break;
                }
            }
        }

        Review result = reviewRepository.updateReview(review);
        final String updateReviewsSuccessMessage = generalMessageAccessor.getMessage(null, UPDATE_REVIEW_SUCCESS, reviewId);
        return new UpdateReviewResponse(updateReviewsSuccessMessage);
    }

    @Override
    public void deleteReview(UUID userId) {
        reviewRepository.deleteAllByUserId(userId);
    }

    private List<Review> createReview(UUID userId, Date startOfDay) {
        List<LectureDto> lectures = lectureInfoService.findLecturesNeedToBeReviewed(userId);
        if (lectures.isEmpty()) {
            return List.of();
        }
        int sectionSize = 0;
        int exerciseSize = 0;

        List<Review> results = new ArrayList<>();
        for (LectureDto lecture : lectures) {
            List<ExerciseReference> exerciseReferences = new ArrayList<>();
            List<SectionReference> sectionReferences = new ArrayList<>();

            for (SectionDto sectionDto : lecture.getSections()) {
                sectionReferences.add(new SectionReference(
                        lecture.getLectureId(),
                        sectionDto.getOrdinalNumber(),
                        false));
            }

            for (ExerciseDto exercise : exerciseInfoService.findExercisesByLectureIdAndStatus(lecture.getLectureId(), ContentStatus.VERIFIED)) {
                exerciseReferences.add(new ExerciseReference(
                        exercise.getExerciseId(),
                        exercise.getOrdinalNumber(),
                        false));
            }

            Review review = Review.builder()
                    .reviewId(UUID.randomUUID())
                    .userId(userId)
                    .ordinalNumber(results.size() + 1)
                    .createdAt(startOfDay)
                    .sectionList(sectionReferences)
                    .exerciseList(exerciseReferences)
                    .build();

            results.add(review);

            sectionSize += sectionReferences.size();
            exerciseSize += exerciseReferences.size();
            if (sectionSize >= 3 && exerciseSize >= 3) {
                break;
            }
        }

        return reviewRepository.saveAll(results);
    }
}
