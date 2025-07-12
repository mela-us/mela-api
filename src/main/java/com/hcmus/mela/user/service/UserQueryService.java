package com.hcmus.mela.user.service;

import com.hcmus.mela.user.dto.request.DeleteProfileRequest;
import com.hcmus.mela.user.dto.response.GetUserDetailResponse;
import com.hcmus.mela.user.dto.response.GetUserReportResponse;
import com.hcmus.mela.user.dto.response.GetUsersResponse;
import com.hcmus.mela.user.model.UserRole;
import com.hcmus.mela.user.repository.UserRepository;
import com.hcmus.mela.user.strategy.UserFilterStrategy;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface UserQueryService {

    GetUsersResponse getUsers(UserFilterStrategy strategy, UUID ownId, UserRole getRole);

    GetUserDetailResponse getUserInfo(UserFilterStrategy strategy, UUID ownId, UUID getUserId);

    Integer countUsersCreateBetween(Date startDate, Date endDate);

    List<UserRepository.MonthlyCount> countUsersCreatedBetweenGroupByMonth(Date startDate, Date endDate);
}

