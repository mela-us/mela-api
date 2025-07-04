package com.hcmus.mela.test.service;

import com.hcmus.mela.test.dto.TestDto;
import com.hcmus.mela.test.model.Question;
import com.hcmus.mela.test.model.TestQuestion;

import java.util.UUID;

public interface TestService {
    TestDto getTestDto(UUID userId);

    Question getQuestionById(UUID questionId);

    TestQuestion getTestByQuestionId(UUID questionId);
}
