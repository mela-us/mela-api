package com.hcmus.mela.user.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.service.LevelInfoService;
import com.hcmus.mela.shared.cache.RedisService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.user.dto.dto.UserDto;
import com.hcmus.mela.user.dto.request.DeleteProfileRequest;
import com.hcmus.mela.user.dto.request.UpdateProfileRequest;
import com.hcmus.mela.user.dto.response.GetProfileResponse;
import com.hcmus.mela.user.exception.UserException;
import com.hcmus.mela.user.exception.UserNotFoundException;
import com.hcmus.mela.user.mapper.UserMapper;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.model.UserRole;
import com.hcmus.mela.user.repository.UserRepository;
import jakarta.transaction.Transactional;
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
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final RedisService redisService;
    private final LevelInfoService levelInfoService;
    private final PasswordEncoder passwordEncoder;
    private final UserDeleteService userDeleteService;

    @Override
    public GetProfileResponse getProfile(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        UserDto userDto = UserMapper.INSTANCE.userToUserDto(user);
        LevelDto level;
        if (user.getLevelId() == null) {
            level = levelInfoService.findLevelByLevelTitle("Lớp 1");
            if (level == null || level.getStatus() != ContentStatus.VERIFIED) {
                level = levelInfoService.findAvailableLevel();
            }
            user.setLevelId(level.getLevelId());
            userRepository.save(user);
        } else {
            level = levelInfoService.findLevelByLevelId(user.getLevelId());
        }
        userDto.setLevelTitle(level.getName());

        return new GetProfileResponse(userDto, "Get user profile successful");
    }

    @Override
    public void updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getFullname() != null) {
            user.setFullname(request.getFullname());
        }
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
        }
        if (request.getLevelTitle() != null) {
            LevelDto level = levelInfoService.findLevelByLevelTitle(request.getLevelTitle());
            if (level == null || level.getStatus() == null || !level.getStatus().equals(ContentStatus.VERIFIED)) {
                throw new UserException("Invalid level title " + request.getLevelTitle());
            }
            user.setLevelId(level.getLevelId());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        Instant nowInstant = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        Date now = Date.from(nowInstant);
        user.setUpdatedAt(now);

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteProfile(UUID userId, DeleteProfileRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        userDeleteService.deleteUserByUserId(user.getUserId(), UserRole.USER);
        redisService.storeAccessToken(request.getAccessToken());
        redisService.storeRefreshToken(request.getRefreshToken());
    }
}