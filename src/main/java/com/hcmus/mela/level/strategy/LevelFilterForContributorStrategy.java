package com.hcmus.mela.level.strategy;

import com.hcmus.mela.lecture.strategy.LectureFilterForContributorStrategy;
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
import java.util.UUID;

@Component("LEVEL_CONTRIBUTOR")
@RequiredArgsConstructor
public class LevelFilterForContributorStrategy implements LevelFilterStrategy {

    private final LevelRepository levelRepository;
    private final UserInfoService userInfoService;

    @Override
    public List<LevelDto> getLevels(UUID userId) {
        List<Level> verifiedLevels = levelRepository.findAllByStatus(ContentStatus.VERIFIED);
        if (verifiedLevels.isEmpty()) {
            return List.of();
        }
        return verifiedLevels.stream()
                .map(level -> {
                    LevelDto levelDto = LevelMapper.INSTANCE.levelToLevelDto(level);
                    if (level.getCreatedBy() != null) {
                        levelDto.setCreator(userInfoService.getUserPreviewDtoByUserId(level.getCreatedBy()));
                    }
                    return levelDto;
                })
                .toList();
    }

    @Override
    public LevelDto createLevel(UUID userId, Level level) {
        return null;
    }

    @Override
    public void updateLevel(UUID userId, UUID levelId, UpdateLevelRequest request) {

    }

    @Override
    public void deleteLevel(UUID userId, UUID levelId) {

    }
}