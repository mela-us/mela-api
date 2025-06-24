package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.response.GetLevelsResponse;
import com.hcmus.mela.lecture.model.Level;
import com.hcmus.mela.lecture.strategy.LevelFilterStrategy;
import com.hcmus.mela.lecture.strategy.TopicFilterStrategy;

import java.util.UUID;

public interface LevelService {

    GetLevelsResponse getLevelsResponse(LevelFilterStrategy levelFilterStrategy, UUID userId);

    Level findLevelByLevelId(UUID id);

    Level findLevelByLevelTitle(String title);
}
