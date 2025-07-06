package com.hcmus.mela.user.service;

import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.user.dto.UserDto;
import com.hcmus.mela.user.dto.request.*;
import com.hcmus.mela.user.dto.response.*;

import java.util.UUID;

public interface UserService {
    UpdateProfileResponse updateProfile(UpdateProfileRequest updateProfileRequest, String authorizationHeader);

    GetUserProfileResponse getUserProfile(String authorizationHeader);

    void deleteAccount(DeleteAccountRequest deleteAccountRequest, String authorizationHeader);

    UUID getLevelId(UUID userId);

    void updateLevel(UUID levelId);
}
