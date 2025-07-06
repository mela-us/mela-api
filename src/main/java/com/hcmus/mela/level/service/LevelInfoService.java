package com.hcmus.mela.level.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.shared.type.ContentStatus;

import java.util.List;
import java.util.UUID;

public interface LevelInfoService {

    LevelDto findLevelByLevelId(UUID levelId);

    LevelDto findLevelByLevelIdAndStatus(UUID levelId, ContentStatus status);

    LevelDto findLevelByLevelTitle(String title);

    LevelDto findAvailableLevel();

    List<LevelDto> findAllLevels();

    List<LevelDto> findAllLevelsInStatus(ContentStatus status);

    void changeLevelOwnerToAdmin(UUID previousUserId);
}
