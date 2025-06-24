package com.hcmus.mela.level.strategy;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.mapper.LevelMapper;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.repository.LevelRepository;
import com.hcmus.mela.shared.exception.BadRequestException;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("LEVEL_ADMIN")
@RequiredArgsConstructor
public class LevelFilterForAdminStrategy implements LevelFilterStrategy {

    private final LevelRepository levelRepository;

    @Override
    public List<LevelDto> getLevels(UUID userId) {
        List<Level> levels = levelRepository.findAll();
        if (levels.isEmpty()) {
            return List.of();
        }
        return levels.stream()
                .map(LevelMapper.INSTANCE::levelToLevelDto)
                .toList();
    }

    @Override
    public void updateLevel(UUID userId, UUID levelId, UpdateLevelRequest updateLevelRequest) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new BadRequestException("Level not found"));
        if (level.getStatus() == ContentStatus.DELETED) {
            throw new BadRequestException("Cannot update a deleted level");
        }
        if (updateLevelRequest.getName() != null && !updateLevelRequest.getName().isEmpty()) {
            level.setName(updateLevelRequest.getName());
        }
        if (updateLevelRequest.getImageUrl() != null && !updateLevelRequest.getImageUrl().isEmpty()) {
            level.setImageUrl(updateLevelRequest.getImageUrl());
        }
        levelRepository.save(level);

    }
}