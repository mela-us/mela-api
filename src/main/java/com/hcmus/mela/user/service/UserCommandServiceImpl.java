package com.hcmus.mela.user.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.service.LevelInfoService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.user.dto.dto.UserDetailDto;
import com.hcmus.mela.user.dto.request.CreateUserRequest;
import com.hcmus.mela.user.dto.request.UpdateUserRequest;
import com.hcmus.mela.user.exception.UserException;
import com.hcmus.mela.user.exception.UserNotFoundException;
import com.hcmus.mela.user.mapper.UserMapper;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.model.UserRole;
import com.hcmus.mela.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final UserDeleteService userDeleteService;
    private final LevelInfoService levelInfoService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        if (request.getUsername() != null && !request.getUsername().isEmpty() && !user.getUsername().equals(request.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserException("Username already exists " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getFullname() != null) {
            user.setFullname(request.getFullname());
        }
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getLevelId() != null) {
            LevelDto level = levelInfoService.findLevelByLevelIdAndStatus(request.getLevelId(), ContentStatus.VERIFIED);
            if (level == null) {
                throw new UserException("Level not found or not verified with id " + request.getLevelId());
            }
            user.setLevelId(level.getLevelId());
        }
        if (request.getUserRole() != null) {
            user.setUserRole(request.getUserRole());
        }
        Instant nowInstant = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        Date now = Date.from(nowInstant);
        user.setUpdatedAt(now);

        userRepository.save(user);
    }

    @Override
    public UserDetailDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserException("Username already exists " + request.getUsername());
        }
        User user = UserMapper.INSTANCE.createUserRequestToUser(request);
        user.setUserId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        LevelDto level = levelInfoService.findLevelByLevelIdAndStatus(user.getLevelId(), ContentStatus.VERIFIED);
        if (level == null) {
            throw new UserException("Level not found or not verified with id " + request.getLevelId());
        }
        Instant nowInstant = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        Date now = Date.from(nowInstant);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        User newUser = userRepository.save(user);
        return UserMapper.INSTANCE.userToUserDetailDto(newUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        if (user.getUserRole() == UserRole.ADMIN) {
            throw new UserException("Cannot delete admin user");
        }
        userDeleteService.deleteUserByUserId(user.getUserId(), user.getUserRole());
    }
}