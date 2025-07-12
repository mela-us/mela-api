package com.hcmus.mela.user.strategy;

import com.hcmus.mela.user.dto.dto.UserDetailDto;
import com.hcmus.mela.user.model.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserFilterStrategy {

    List<UserDetailDto> getUsers(UUID ownUserId, UserRole roleToGet);

    UserDetailDto getUserInfo(UUID ownUserId, UUID userIdToGet);
}