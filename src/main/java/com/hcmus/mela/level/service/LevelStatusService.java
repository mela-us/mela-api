package com.hcmus.mela.level.service;

import com.hcmus.mela.shared.type.ContentStatus;

import java.util.UUID;

public interface LevelStatusService {

    void denyLevel(UUID levelId, String reason);

    void approveLevel(UUID levelId);

    boolean isLevelAssignableToLecture(UUID userId, UUID levelId);

    boolean isLevelInStatus(UUID levelId, ContentStatus status);
}
