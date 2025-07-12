package com.hcmus.mela.level.strategy;

import com.hcmus.mela.lecture.strategy.LectureFilterForAdminStrategy;
import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.request.UpdateLevelRequest;
import com.hcmus.mela.level.exception.LevelException;
import com.hcmus.mela.level.mapper.LevelMapper;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.repository.LevelRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component("LEVEL_ADMIN")
@RequiredArgsConstructor
public class LevelFilterForAdminStrategy implements LevelFilterStrategy {

    private final LevelRepository levelRepository;
    private final LectureFilterForAdminStrategy lectureFilterStrategy;
    private final UserInfoService userInfoService;


    @Override
    public List<LevelDto> getLevels(UUID userId) {
        List<Level> levels = levelRepository.findAll();
        if (levels.isEmpty()) {
            return List.of();
        }
        return levels.stream()
                .filter(level -> level.getStatus() != ContentStatus.DELETED)
                .map(level -> {
                    LevelDto levelDto = LevelMapper.INSTANCE.levelToLevelDto(level);
                    if (level.getCreatedBy() != null) {
                        levelDto.setCreator(userInfoService.getUserPreviewDtoByUserId(level.getCreatedBy()));
                    }
                    return levelDto;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public LevelDto createLevel(UUID userId, Level level) {
        if (level.getLevelId() == null) {
            level.setLevelId(UUID.randomUUID());
        }
        level.setCreatedBy(null);
        if (level.getStatus() == null) {
            level.setStatus(ContentStatus.PENDING);
        }
        Level savedLevel = levelRepository.save(level);
        return LevelMapper.INSTANCE.levelToLevelDto(savedLevel);
    }

    @Override
    public void updateLevel(UUID userId, UUID levelId, UpdateLevelRequest request) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelException("Level not found in the system"));
        if (level.getStatus() == ContentStatus.DELETED) {
            throw new LevelException("Cannot update a deleted level");
        }
        if (request.getName() != null && !request.getName().isEmpty()) {
            level.setName(request.getName());
        }
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            level.setImageUrl(request.getImageUrl());
        }
        if (level.getStatus() == ContentStatus.DENIED) {
            level.setStatus(ContentStatus.PENDING);
            level.setRejectedReason(null);
        }
        levelRepository.save(level);
    }

    @Override
    public void deleteLevel(UUID userId, UUID levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelException("Level not found in the system"));
        if (level.getStatus() == ContentStatus.DELETED) {
            return;
        }
        lectureFilterStrategy.deleteLecturesByLevel(userId, levelId);
        level.setStatus(ContentStatus.DELETED);
        levelRepository.save(level);
        userInfoService.updateLevelForAllUser(levelId);
    }
}