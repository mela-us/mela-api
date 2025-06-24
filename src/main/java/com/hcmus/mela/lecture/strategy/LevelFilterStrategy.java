package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.lecture.dto.dto.LevelDto;
import com.hcmus.mela.lecture.dto.dto.TopicDto;

import java.util.List;
import java.util.UUID;

public interface LevelFilterStrategy {
    List<LevelDto> getLevels(UUID userId);
}