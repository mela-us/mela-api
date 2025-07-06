package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.response.GetLevelsResponse;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;

import java.util.UUID;

public interface LevelQueryService {

    GetLevelsResponse getLevelsResponse(LevelFilterStrategy levelFilterStrategy, UUID userId);
}
