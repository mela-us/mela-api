package com.hcmus.mela.user.service;

import com.hcmus.mela.user.dto.request.DeleteProfileRequest;
import com.hcmus.mela.user.dto.request.UpdateProfileRequest;
import com.hcmus.mela.user.dto.response.GetProfileResponse;

import java.util.UUID;

public interface ProfileService {

    GetProfileResponse getProfile(UUID userId);

    void updateProfile(UUID userId, UpdateProfileRequest request);

    void deleteProfile(UUID userId, DeleteProfileRequest request);
}
