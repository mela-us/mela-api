package com.hcmus.mela.user.service;

import com.hcmus.mela.ai.chat.service.ConversationHistoryService;
import com.hcmus.mela.exercise.service.ExerciseInfoService;
import com.hcmus.mela.history.service.ExerciseHistoryService;
import com.hcmus.mela.history.service.LectureHistoryService;
import com.hcmus.mela.lecture.service.LectureInfoService;
import com.hcmus.mela.level.service.LevelInfoService;
import com.hcmus.mela.review.service.ReviewService;
import com.hcmus.mela.skills.service.UserSkillService;
import com.hcmus.mela.streak.service.StreakService;
import com.hcmus.mela.suggestion.service.SuggestionService;
import com.hcmus.mela.token.service.TokenService;
import com.hcmus.mela.topic.service.TopicInfoService;
import com.hcmus.mela.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeleteServiceImpl implements UserDeleteService {

    private final UserRepository userRepository;
    private final TopicInfoService topicInfoService;
    private final LevelInfoService levelInfoService;
    private final LectureInfoService lectureInfoService;
    private final ExerciseInfoService exerciseInfoService;
    private final ExerciseHistoryService exerciseHistoryService;
    private final LectureHistoryService lectureHistoryService;
    private final ConversationHistoryService conversationHistoryService;
    private final SuggestionService suggestionService;
    private final ReviewService reviewService;
    private final UserSkillService userSkillService;
    private final TokenService tokenService;
    private final StreakService streakService;

    @Override
    @Transactional
    public void deleteUserByUserId(UUID userId) {
        log.info("Changing content management ownership for user {} to admin (if possible)", userId);
        topicInfoService.changeTopicOwnerToAdmin(userId);
        levelInfoService.changeLevelOwnerToAdmin(userId);
        lectureInfoService.changeLectureOwnerToAdmin(userId);
        exerciseInfoService.changeExerciseOwnerToAdmin(userId);
        log.info("Delete study data for user {}", userId);
        exerciseHistoryService.deleteAllExerciseHistoryByUserId(userId);
        lectureHistoryService.deleteAllLectureHistoryByUserId(userId);
        conversationHistoryService.deleteConversationByUserId(userId);
        suggestionService.deleteSuggestion(userId);
        reviewService.deleteReview(userId);
        userSkillService.deleteUserSkillByUserId(userId);
        tokenService.deleteUserToken(userId);
        streakService.deleteStreak(userId);
        log.info("Delete user with id {}", userId);
        userRepository.deleteById(userId);
    }
}