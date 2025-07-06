package com.hcmus.mela.user.service;

import com.hcmus.mela.user.dto.dto.UserDetailDto;
import com.hcmus.mela.user.dto.response.GetUserDetailResponse;
import com.hcmus.mela.user.dto.response.GetUserReportResponse;
import com.hcmus.mela.user.dto.response.GetUsersResponse;
import com.hcmus.mela.user.exception.UserNotFoundException;
import com.hcmus.mela.user.mapper.UserMapper;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.model.UserRole;
import com.hcmus.mela.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public GetUsersResponse getUsers(UserRole role) {
        List<User> users = new ArrayList<>();
        if (role == null) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findAllByUserRole(role);
        }
        List<UserDetailDto> userDetailDtos = users.stream()
                .filter(user -> user.getUserRole() != UserRole.ADMIN)
                .map(UserMapper.INSTANCE::userToUserDetailDto)
                .toList();
        return new GetUsersResponse("Get users successfully", userDetailDtos);
    }

    @Override
    public GetUserDetailResponse getUserInfo(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        UserDetailDto userDetailDto = UserMapper.INSTANCE.userToUserDetailDto(user);
        return new GetUserDetailResponse("Get user info successfully", userDetailDto);
    }

    @Override
    public GetUserReportResponse getUserReport(UUID userId) {
        return new GetUserReportResponse("Get user report successfully");
    }
}