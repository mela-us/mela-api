package com.hcmus.mela.user.service;

import com.hcmus.mela.user.dto.dto.UserDto;
import com.hcmus.mela.user.dto.dto.UserPreviewDto;
import com.hcmus.mela.user.model.User;

import java.util.UUID;

public interface UserInfoService {

    User getUserByUserId(UUID userId);

    UserDto getUserDtoByUserId(UUID userId);

    UUID getLevelIdOfUser(UUID userId);

    void updateLevelForAllUser(UUID oldLevelId);

    UserPreviewDto getUserPreviewDtoByUserId(UUID userId);
}
