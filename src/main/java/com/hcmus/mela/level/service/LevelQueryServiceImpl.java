package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.response.GetLevelsResponse;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelQueryServiceImpl implements LevelQueryService {

    public GetLevelsResponse getLevelsResponse(LevelFilterStrategy strategy, UUID userId) {
        List<LevelDto> levels =  new ArrayList<>(strategy.getLevels(userId));
        if (levels.isEmpty()) {
            return new GetLevelsResponse("No levels found", 0, Collections.emptyList());
        }
        levels.sort((l1, l2) -> l1.getName().compareToIgnoreCase(l2.getName()));
        return new GetLevelsResponse("Levels retrieved successfully", levels.size(), levels);
    }
}
