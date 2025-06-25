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
public class LevelServiceImpl implements LevelService {

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
    public CreateLevelResponse getCreateLevelResponse(UUID creatorId, CreateLevelRequest createLevelRequest) {
        Level level = LevelMapper.INSTANCE.createLevelRequestToLevel(createLevelRequest);
        level.setLevelId(UUID.randomUUID());
        level.setCreatedBy(creatorId);
        level.setStatus(ContentStatus.PENDING);
        Level savedLevel = levelRepository.save(level);

        LevelDto levelDto = LevelMapper.INSTANCE.levelToLevelDto(savedLevel);

        return new CreateLevelResponse(
                "Create level successfully",
                levelDto
        );
    }

    @Override
    public void updateLevel(LevelFilterStrategy strategy, UUID userId, UUID levelId, UpdateLevelRequest request) {
        strategy.updateLevel(userId, levelId, request);
    }

    @Override
    public void denyLevel(UUID levelId, String reason) {
        Level level = levelRepository.findById(levelId).orElseThrow(() -> new LevelException("Level not found"));
        if (level.getStatus() == ContentStatus.VERIFIED || level.getStatus() == ContentStatus.DELETED) {
            throw new LevelException("Level cannot be denied");
        }
        level.setRejectedReason(reason);
        level.setStatus(ContentStatus.DENIED);
        levelRepository.save(level);
    }

    @Override
    public void approveLevel(UUID levelId) {
        Level level = levelRepository.findById(levelId).orElseThrow(() -> new LevelException("Level not found"));
        if (level.getStatus() == ContentStatus.DELETED) {
            throw new LevelException("Level cannot be approved");
        }
        level.setRejectedReason(null);
        level.setStatus(ContentStatus.VERIFIED);
        levelRepository.save(level);
    }

    @Override
    public boolean isLevelAssignableToLecture(UUID levelId, UUID userId) {
        if (levelId == null || userId == null) {
            return false;
        }
        Level level = levelRepository.findById(levelId).orElse(null);
        if (level == null) {
            return false;
        }
        if (level.getStatus() == ContentStatus.VERIFIED) {
            return true;
        }
        return level.getStatus() != ContentStatus.DELETED && level.getCreatedBy().equals(userId);
    }

    @Override
    public boolean isLevelDeleted(UUID levelId) {
        return checkLevelStatus(levelId, ContentStatus.DELETED);
    }

    @Override
    public boolean isLevelVerified(UUID levelId) {
        return checkLevelStatus(levelId, ContentStatus.VERIFIED);
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
        return levelRepository.findByName(title);
    }
}
