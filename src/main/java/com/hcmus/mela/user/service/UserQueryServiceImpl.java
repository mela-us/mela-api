package com.hcmus.mela.user.service;

import com.hcmus.mela.user.dto.dto.UserDetailDto;
import com.hcmus.mela.user.dto.response.GetUserDetailResponse;
import com.hcmus.mela.user.dto.response.GetUsersResponse;
import com.hcmus.mela.user.model.UserRole;
import com.hcmus.mela.user.repository.UserRepository;
import com.hcmus.mela.user.strategy.UserFilterStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public GetUsersResponse getUsers(UserFilterStrategy strategy, UUID ownId, UserRole role) {
        List<UserDetailDto> userDetailDtos = strategy.getUsers(ownId, role);
        return new GetUsersResponse("Get users successfully", userDetailDtos);
    }

    @Override
    public GetUserDetailResponse getUserInfo(UserFilterStrategy strategy, UUID ownId, UUID getUserId) {
        UserDetailDto userDetailDto = strategy.getUserInfo(ownId, getUserId);
        return new GetUserDetailResponse("Get user info successfully", userDetailDto);
    }

    @Override
    public Integer countUsersCreateBetween(Date startDate, Date endDate) {
        return userRepository.countByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public List<UserRepository.MonthlyCount> countUsersCreatedBetweenGroupByMonth(Date startDate, Date endDate) {
        return userRepository.countByMonthInPeriod(startDate, endDate);
    }
}