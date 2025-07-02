package com.hcmus.mela.test.service;

import com.hcmus.mela.test.dto.TestDto;

import java.util.UUID;

public interface TestService {
    TestDto getTestDto(UUID userId);

}
