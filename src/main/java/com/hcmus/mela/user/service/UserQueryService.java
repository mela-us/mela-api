package com.hcmus.mela.user.service;

import com.hcmus.mela.user.dto.request.DeleteProfileRequest;
import com.hcmus.mela.user.dto.response.GetUserDetailResponse;
import com.hcmus.mela.user.dto.response.GetUserReportResponse;
import com.hcmus.mela.user.dto.response.GetUsersResponse;
import com.hcmus.mela.user.model.UserRole;

import java.util.UUID;

public interface UserQueryService {

    GetUsersResponse getUsers(UserRole role);

    GetUserDetailResponse getUserInfo(UUID userId);

    GetUserReportResponse getUserReport(UUID userId);
}
