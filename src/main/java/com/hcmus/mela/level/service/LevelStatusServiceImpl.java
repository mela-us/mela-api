package com.hcmus.mela.level.service;

import com.hcmus.mela.level.exception.LevelException;
import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.repository.LevelRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelStatusServiceImpl implements LevelStatusService {

    private final LevelRepository levelRepository;

    @Override
    public void denyLevel(UUID levelId, String reason) {
        Level level = levelRepository.findById(levelId).orElseThrow(() -> new LevelException("Level not found in the system"));
        if (level.getStatus() == ContentStatus.VERIFIED || level.getStatus() == ContentStatus.DELETED) {
            throw new LevelException("Verified or deleted level cannot be denied");
        }
        level.setRejectedReason(reason);
        level.setStatus(ContentStatus.DENIED);
        levelRepository.save(level);
    }

    @Override
    public void approveLevel(UUID levelId) {
        Level level = levelRepository.findById(levelId).orElseThrow(() -> new LevelException("Level not found in the system"));
        if (level.getStatus() == ContentStatus.DELETED) {
            throw new LevelException("Deleted level cannot be approved");
        }
        level.setRejectedReason(null);
        level.setStatus(ContentStatus.VERIFIED);
        levelRepository.save(level);
    }

    @Override
    public boolean isLevelAssignableToLecture(UUID userId, UUID levelId) {
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
        return level.getStatus() != ContentStatus.DELETED && userId.equals(level.getCreatedBy());
    }

    @Override
    public boolean isLevelInStatus(UUID levelId, ContentStatus status) {
        if (levelId == null || status == null) {
            return false;
        }
        Level level = levelRepository.findById(levelId).orElse(null);
        return level != null && level.getStatus() == status;
    }
}
