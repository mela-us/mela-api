package com.hcmus.mela.history.service;

import com.hcmus.mela.history.dto.dto.AnswerResultDto;
import com.hcmus.mela.history.dto.dto.TestHistoryDto;
import com.hcmus.mela.history.dto.request.TestResultRequest;
import com.hcmus.mela.history.dto.response.TestResultResponse;
import com.hcmus.mela.history.mapper.TestAnswerMapper;
import com.hcmus.mela.history.mapper.TestHistoryMapper;
import com.hcmus.mela.history.model.TestAnswer;
import com.hcmus.mela.history.model.TestHistory;
import com.hcmus.mela.history.repository.TestHistoryRepository;
import com.hcmus.mela.skills.service.UserSkillService;
import com.hcmus.mela.test.model.TestQuestion;
import com.hcmus.mela.test.repository.TestQuestionRepository;
import com.hcmus.mela.test.service.TestGradeService;
import com.hcmus.mela.test.service.TestService;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class TestHistoryServiceImpl implements TestHistoryService {

    private final TestGradeService testGradeService;
    private final UserService userService;
    private final TestQuestionRepository testQuestionRepository;
    private final TestHistoryRepository testHistoryRepository;
    private final UserSkillService userSkillService;
    private final TestService testService;

    @Override
    public TestResultResponse getTestResultResponse(UUID userId, TestResultRequest request) {
        List<TestAnswer> testAnswerList = testGradeService.gradeTest(request.getAnswers());

        saveTestHistory(userId, request.getStartedAt(), request.getCompletedAt(), testAnswerList);

        log.info("Test result saved successfully for user: {}", userId);

        List<AnswerResultDto> answerResults = testAnswerList.stream()
                .map(TestAnswerMapper.INSTANCE::convertToAnswerResultDto)
                .toList();

        return new TestResultResponse(
                "Test result submit successfully for user: " + userId,
                answerResults);
    }

    @Override
    public List<TestHistoryDto> getTestHistoryByUserAndLevel(UUID userId, UUID levelId) {
        List<TestHistory> testHistoryList = testHistoryRepository.findAllByUserIdAndLevelId(userId, levelId);
        if (testHistoryList == null || testHistoryList.isEmpty()) {
            return new ArrayList<>();
        }
        return testHistoryList.stream()
                .map(TestHistoryMapper.INSTANCE::convertToTestHistoryDto)
                .toList();
    }

    private void saveTestHistory(UUID userId, LocalDateTime startedAt, LocalDateTime completedAt, List<TestAnswer> answers) {
        Double score = answers.stream()
                .filter(TestAnswer::getIsCorrect)
                .count() * 1.0 / answers.size() * 100;

        TestHistory testHistory = TestHistory.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .levelId(userService.getLevelId(userId))
                .score(score)
                .startedAt(startedAt)
                .completedAt(completedAt)
                .answers(answers)
                .build();

        testHistoryRepository.save(testHistory);

        for (TestAnswer testAnswer : answers) {
            TestQuestion testQuestion = testService.getTestByQuestionId(testAnswer.getQuestionId());

            int correctAnswer = testAnswer.getIsCorrect() ? 1 : 0;

            userSkillService.updateUserSkill(userId,
                    userService.getLevelId(userId),
                    testQuestion.getTopicId(),
                    correctAnswer * 2,
                    1 - correctAnswer);
        }
    }
}
