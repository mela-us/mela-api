package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.request.CreateLevelRequest;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.dto.response.CreateLevelResponse;
import com.hcmus.mela.level.mapper.LevelMapper;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.repository.LevelRepository;
import com.hcmus.mela.level.strategy.LevelFilterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelCommandServiceImpl implements LevelCommandService {

    @Override
    public CreateLevelResponse createLevel(LevelFilterStrategy strategy, UUID userId, CreateLevelRequest request) {
        Level level = LevelMapper.INSTANCE.createLevelRequestToLevel(request);
        LevelDto levelDto = strategy.createLevel(userId, level);
        return new CreateLevelResponse("Create level successfully", levelDto);
    }

    @Override
    public void updateLevel(LevelFilterStrategy strategy, UUID userId, UUID levelId, UpdateLevelRequest request) {
        strategy.updateLevel(userId, levelId, request);
    }

    @Transactional
    @Override
    public void deleteLevel(LevelFilterStrategy strategy, UUID userId, UUID levelId) {
        strategy.deleteLevel(userId, levelId);
    }
}
