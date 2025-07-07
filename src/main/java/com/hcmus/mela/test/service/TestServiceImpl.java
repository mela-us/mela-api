package com.hcmus.mela.test.service;

import com.hcmus.mela.level.service.LevelStatusService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.test.dto.TestDto;
import com.hcmus.mela.test.mapper.QuestionMapper;
import com.hcmus.mela.test.model.Question;
import com.hcmus.mela.test.model.TestQuestion;
import com.hcmus.mela.test.repository.TestQuestionRepository;
import com.hcmus.mela.topic.service.TopicStatusService;
import com.hcmus.mela.user.service.UserInfoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TestServiceImpl implements TestService {

    private final static int QUESTION_CAPACITY = 20;
    private final TestQuestionRepository testQuestionRepository;
    private final UserInfoService userInfoService;
    private final TopicStatusService topicStatusService;
    private final LevelStatusService levelStatusService;

    @Override
    public TestDto getTestDto(UUID userId) {
        UUID levelId = userInfoService.getLevelIdOfUser(userId);

        List<TestQuestion> testQuestionList = testQuestionRepository.findAllByLevelId(levelId);
        testQuestionList.removeIf(testQuestion -> !topicStatusService.isTopicInStatus(testQuestion.getTopicId(), ContentStatus.VERIFIED));
        List<Question> finalQuestions = new ArrayList<>();

        int numberOfTopics = testQuestionList.size();
        int remainingSlots = QUESTION_CAPACITY - numberOfTopics;

        // Step 1: Pick 1 question from each topic to ensure all topics are represented
        List<List<Question>> candidatePools = new ArrayList<>();
        for (TestQuestion testQuestion : testQuestionList) {
            List<Question> questions = testQuestion.getQuestions();
            if (questions == null || questions.isEmpty()) continue;

            List<Question> shuffled = new ArrayList<>(questions);
            Collections.shuffle(shuffled);
            finalQuestions.add(shuffled.get(0)); // Add the first (randomized) question
            shuffled.remove(0); // Remove the selected question
            candidatePools.add(shuffled); // Store remaining questions for step 2
        }

        // Step 2: Fill remaining slots by distributing extra questions from each topic evenly
        outer:
        while (remainingSlots > 0) {
            for (List<Question> pool : candidatePools) {
                if (remainingSlots <= 0) break outer;
                if (!pool.isEmpty()) {
                    finalQuestions.add(pool.remove(0));
                    remainingSlots--;
                }
            }
        }

        return TestDto.builder()
                .questions(QuestionMapper.INSTANCE.questionsToQuestionDtoList(finalQuestions))
                .total(finalQuestions.size())
                .build();
    }

    @Override
    public Question getQuestionById(UUID questionId) {
        TestQuestion test = testQuestionRepository.findByQuestionsQuestionId(questionId);
        if (test == null) return null;
        if (!topicStatusService.isTopicInStatus(test.getTopicId(), ContentStatus.VERIFIED)) {
            return null; // Topic is not in VERIFIED status
        }
        if (!levelStatusService.isLevelInStatus(test.getLevelId(), ContentStatus.VERIFIED)) {
            return null; // Level is not in VERIFIED status
        }
        return test.getQuestions().stream()
                .filter(q -> q.getQuestionId().equals(questionId))
                .findFirst().orElse(null);
    }

    @Override
    public TestQuestion getTestByQuestionId(UUID questionId) {
        TestQuestion test = testQuestionRepository.findByQuestionsQuestionId(questionId);
        if (!topicStatusService.isTopicInStatus(test.getTopicId(), ContentStatus.VERIFIED)) {
            return null; // Topic is not in VERIFIED status
        }
        if (!levelStatusService.isLevelInStatus(test.getLevelId(), ContentStatus.VERIFIED)) {
            return null; // Level is not in VERIFIED status
        }
        return test;
    }
}
