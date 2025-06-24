package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.lecture.dto.dto.LevelDto;
import com.hcmus.mela.lecture.mapper.LevelMapper;
import com.hcmus.mela.lecture.model.Level;
import com.hcmus.mela.lecture.repository.LevelRepository;
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
}