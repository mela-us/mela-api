package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.lecture.dto.dto.LevelDto;
import com.hcmus.mela.lecture.mapper.LevelMapper;
import com.hcmus.mela.lecture.model.Level;
import com.hcmus.mela.lecture.repository.LevelRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("LEVEL_CONTRIBUTOR")
@RequiredArgsConstructor
public class LevelFilterForContributorStrategy implements LevelFilterStrategy {

    private final LevelRepository levelRepository;

    @Override
    public List<LevelDto> getLevels(UUID userId) {
        List<Level> verifiedLevels = levelRepository.findAllByStatus(ContentStatus.VERIFIED);
        List<Level> pendingLevels = levelRepository.findAllByStatusAndCreatedBy(ContentStatus.PENDING, userId);
        List<Level> deniedLevels = levelRepository.findAllByStatusAndCreatedBy(ContentStatus.DENIED, userId);
        // Combine all levels
        verifiedLevels.addAll(pendingLevels);
        verifiedLevels.addAll(deniedLevels);
        if (verifiedLevels.isEmpty()) {
            return List.of();
        }
        return verifiedLevels.stream()
                .map(LevelMapper.INSTANCE::levelToLevelDto)
                .toList();
    }
}