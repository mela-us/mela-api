package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.request.CreateLevelRequest;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.dto.response.CreateLevelResponse;
import com.hcmus.mela.level.dto.response.GetLevelsResponse;
import com.hcmus.mela.level.exception.LevelException;
import com.hcmus.mela.level.mapper.LevelMapper;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.repository.LevelRepository;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelQueryServiceImpl implements LevelQueryService {

    private final LevelRepository levelRepository;

    private final GeneralMessageAccessor generalMessageAccessor;

    public GetLevelsResponse getLevelsResponse(LevelFilterStrategy levelFilterStrategy, UUID userId) {

        List<LevelDto> levels = levelFilterStrategy.getLevels(userId);

        if (levels.isEmpty()) {
            return new GetLevelsResponse(
                    generalMessageAccessor.getMessage(null, "get_levels_empty"),
                    0,
                    Collections.emptyList()
            );
        }

        return new GetLevelsResponse(
                generalMessageAccessor.getMessage(null, "get_levels_success"),
                levels.size(),
                levels
        );
    }

    @Override
    public boolean checkLevelStatus(UUID levelId, ContentStatus status) {
        if (levelId == null || status == null) {
            return false;
        }
        Level level = levelRepository.findById(levelId).orElse(null);
        return level != null && level.getStatus() == status;
    }

    @Override
    public Level findLevelByLevelId(UUID id) {
        return levelRepository.findById(id).orElse(null);
    }

    @Override
    public Level findLevelByLevelTitle(String title) {
        return levelRepository.findByName(title).orElse(null);
    }
}
