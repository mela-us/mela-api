package com.hcmus.mela.user.service;

import com.hcmus.mela.user.model.UserRole;

import java.util.UUID;

public interface UserDeleteService {

    void deleteUserByUserId(UUID userId, UserRole userRole);
}
