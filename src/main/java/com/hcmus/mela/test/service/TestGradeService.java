package com.hcmus.mela.test.service;

import com.hcmus.mela.history.dto.dto.TestAnswerDto;
import com.hcmus.mela.history.model.TestAnswer;

import java.util.List;
import java.util.Map;

public interface TestGradeService {

    List<TestAnswer> gradeTest(List<TestAnswerDto> testAnswerList);

    Map<String, Object> checkQuestionAnswer(TestAnswerDto answerDto);
}
