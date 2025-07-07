package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.response.GetLevelsResponse;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelQueryServiceImpl implements LevelQueryService {

    public GetLevelsResponse getLevelsResponse(LevelFilterStrategy levelFilterStrategy, UUID userId) {
        List<LevelDto> levels = levelFilterStrategy.getLevels(userId);
        if (levels.isEmpty()) {
            return new GetLevelsResponse("No levels found", 0, Collections.emptyList());
        }
        return new GetLevelsResponse("Levels retrieved successfully", levels.size(), levels);
    }
}
