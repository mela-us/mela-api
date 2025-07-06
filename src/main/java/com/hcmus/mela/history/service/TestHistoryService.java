package com.hcmus.mela.history.service;

import com.hcmus.mela.history.dto.dto.TestHistoryDto;
import com.hcmus.mela.history.dto.request.TestResultRequest;
import com.hcmus.mela.history.dto.response.TestResultResponse;

import java.util.List;
import java.util.UUID;

public interface TestHistoryService {

    TestResultResponse getTestResultResponse(UUID userId, TestResultRequest request);

    List<TestHistoryDto> getTestHistoryByUserAndLevel(UUID userId, UUID levelId);
}
