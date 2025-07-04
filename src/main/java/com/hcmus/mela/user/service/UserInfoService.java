package com.hcmus.mela.user.service;

import com.hcmus.mela.user.dto.UserDto;
import com.hcmus.mela.user.model.User;

import java.util.UUID;

public interface UserInfoService {
    UUID getLevelOfUser(UUID userId);

    User getUserById(UUID userId);
}
