package com.hcmus.mela.level.strategy;

import com.hcmus.mela.lecture.strategy.LectureFilterForContributorStrategy;
import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.exception.LevelException;
import com.hcmus.mela.level.mapper.LevelMapper;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.repository.LevelRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("LEVEL_CONTRIBUTOR")
@RequiredArgsConstructor
public class LevelFilterForContributorStrategy implements LevelFilterStrategy {

    private final LevelRepository levelRepository;

    private final LectureFilterForContributorStrategy lectureFilterStrategy;

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

    @Override
    public void updateLevel(UUID userId, UUID levelId, UpdateLevelRequest updateLevelRequest) {
        Level level = levelRepository.findByLevelIdAndCreatedBy(levelId, userId)
                .orElseThrow(() -> new LevelException("Level of the contributor not found"));
        if (level.getStatus() == ContentStatus.DELETED || level.getStatus() == ContentStatus.VERIFIED) {
            throw new LevelException("Contributor cannot update a deleted or verified level");
        }
        if (updateLevelRequest.getName() != null && !updateLevelRequest.getName().isEmpty()) {
            level.setName(updateLevelRequest.getName());
        }
        if (updateLevelRequest.getImageUrl() != null && !updateLevelRequest.getImageUrl().isEmpty()) {
            level.setImageUrl(updateLevelRequest.getImageUrl());
        }
        level.setStatus(ContentStatus.PENDING);
        level.setRejectedReason(null);
        levelRepository.save(level);
    }

    @Override
    public void deleteLevel(UUID userId, UUID levelId) {
        Level level = levelRepository.findByLevelIdAndCreatedBy(levelId, userId)
                .orElseThrow(() -> new LevelException("Level not found"));
        if (level.getStatus() == ContentStatus.VERIFIED) {
            throw new LevelException("Contributor cannot delete a verified level");
        }
        lectureFilterStrategy.deleteLecturesByLevel(userId, levelId);
        level.setStatus(ContentStatus.DELETED);
        levelRepository.save(level);
    }
}