package com.hcmus.mela.user.service;

import com.hcmus.mela.user.dto.request.CreateUserRequest;
import com.hcmus.mela.user.dto.request.UpdateUserRequest;

import java.util.UUID;

public interface UserCommandService {

    void updateUser(UUID userId, UpdateUserRequest request);

    void deleteUser(UUID userId);

    void createUser(CreateUserRequest request);
}
