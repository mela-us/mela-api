package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.request.CreateLevelRequest;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.dto.response.CreateLevelResponse;
import com.hcmus.mela.level.mapper.LevelMapper;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.repository.LevelRepository;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelCommandServiceImpl implements LevelCommandService {

    private final LevelRepository levelRepository;

    @Override
    public CreateLevelResponse createLevel(UUID userId, CreateLevelRequest request) {
        Level level = LevelMapper.INSTANCE.createLevelRequestToLevel(request);
        level.setLevelId(UUID.randomUUID());
        level.setCreatedBy(userId);
        level.setStatus(ContentStatus.PENDING);
        Level savedLevel = levelRepository.save(level);
        LevelDto levelDto = LevelMapper.INSTANCE.levelToLevelDto(savedLevel);
        return new CreateLevelResponse("Create level successfully", levelDto);
    }

    @Override
    public void updateLevel(LevelFilterStrategy strategy, UUID userId, UUID levelId, UpdateLevelRequest request) {
        strategy.updateLevel(userId, levelId, request);
    }

    @Override
    public void deleteLevel(LevelFilterStrategy strategy, UUID userId, UUID levelId) {
        strategy.deleteLevel(userId, levelId);
    }
}
