package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.mapper.LevelMapper;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.repository.LevelRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelInfoServiceImpl implements LevelInfoService {

    private final LevelRepository levelRepository;

    @Override
    public LevelDto findLevelByLevelId(UUID levelId) {
        Level level = levelRepository.findById(levelId).orElse(null);
        return level == null ? null : LevelMapper.INSTANCE.levelToLevelDto(level);
    }

    @Override
    public LevelDto findLevelByLevelIdAndStatus(UUID id, ContentStatus status) {
        Level level = levelRepository.findByLevelIdAndStatus(id, status).orElse(null);
        return level == null ? null : LevelMapper.INSTANCE.levelToLevelDto(level);
    }

    @Override
    public LevelDto findLevelByLevelTitle(String title) {
        Level level = levelRepository.findByName(title).orElse(null);
        return level == null ? null : LevelMapper.INSTANCE.levelToLevelDto(level);
    }

    @Override
    public List<LevelDto> findAllLevels() {
        List<Level> levels = levelRepository.findAll();
        return levels.isEmpty()
                ? List.of()
                : levels.stream().map(LevelMapper.INSTANCE::levelToLevelDto).toList();
    }

    @Override
    public List<LevelDto> findAllLevelsInStatus(ContentStatus status) {
        List<Level> levels = levelRepository.findAllByStatus(status);
        return levels.isEmpty()
                ? List.of()
                : levels.stream().map(LevelMapper.INSTANCE::levelToLevelDto).toList();
    }
}
