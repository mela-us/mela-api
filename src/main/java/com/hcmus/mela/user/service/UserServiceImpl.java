package com.hcmus.mela.user.service;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.auth.service.OtpService;
import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.service.LevelInfoService;
import com.hcmus.mela.level.service.LevelStatusService;
import com.hcmus.mela.shared.cache.RedisService;
import com.hcmus.mela.shared.exception.BadRequestException;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.ExceptionMessageAccessor;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.user.dto.UserDto;
import com.hcmus.mela.user.dto.request.DeleteAccountRequest;
import com.hcmus.mela.user.dto.request.UpdateProfileRequest;
import com.hcmus.mela.user.dto.response.GetUserProfileResponse;
import com.hcmus.mela.user.dto.response.UpdateProfileResponse;
import com.hcmus.mela.user.exception.EmptyUpdateDataException;
import com.hcmus.mela.user.exception.UserNotFoundException;
import com.hcmus.mela.user.mapper.UserMapper;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final RedisService redisService;
    private final JwtTokenService jwtTokenService;
    private final GeneralMessageAccessor generalMessageAccessor;
    private final ExceptionMessageAccessor exceptionMessageAccessor;
    private final LevelInfoService levelInfoService;
    private final LevelStatusService levelStatusService;

    @Override
    public UpdateProfileResponse updateProfile(UpdateProfileRequest updateProfileRequest, String authorizationHeader) {

        final UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        User user = userRepository.findByUserId(userId).orElse(null);

        if (user == null) {
            final String userNotFound = exceptionMessageAccessor.getMessage(null, "user_not_found");
            throw new BadRequestException(userNotFound);
        }

        if (updateProfileRequest.getBirthday() == null &&
                updateProfileRequest.getFullname() == null &&
                updateProfileRequest.getImageUrl() == null &&
                updateProfileRequest.getLevelTitle() == null) {
            final String noDataToUpdate = exceptionMessageAccessor.getMessage(null, "no_data_to_update");
            throw new EmptyUpdateDataException(noDataToUpdate);
        }

        if (updateProfileRequest.getBirthday() != null) {
            user.setBirthday(updateProfileRequest.getBirthday());
        }

        if (updateProfileRequest.getFullname() != null) {
            user.setFullname(updateProfileRequest.getFullname());
        }

        if (updateProfileRequest.getImageUrl() != null) {
            user.setImageUrl(updateProfileRequest.getImageUrl());
        }

        if (updateProfileRequest.getLevelTitle() != null) {
            LevelDto level = levelInfoService.findLevelByLevelTitle(updateProfileRequest.getLevelTitle());
            user.setLevelId(level.getLevelId());
        }

        user.setUpdatedAt(new Date());

        userRepository.save(user);

        final String updatedSuccessfully = generalMessageAccessor.getMessage(null, "update_user_successful");

        return new UpdateProfileResponse(updatedSuccessfully);
    }

    @Override
    public GetUserProfileResponse getUserProfile(String authorizationHeader) {

        final UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        User user = userRepository.findByUserId(userId).orElse(null);

        if (user == null) {
            final String userNotFound = exceptionMessageAccessor.getMessage(null, "user_not_found");
            throw new BadRequestException(userNotFound);
        }
        UserDto userDto = UserMapper.INSTANCE.userToUserDto(user);
        if (user.getLevelId() == null) {
            LevelDto level = levelInfoService.findLevelByLevelTitle("Lớp 1");
            user.setLevelId(level.getLevelId());
            userRepository.save(user);
            userDto.setLevelTitle(level.getName());
        } else {
            LevelDto level = levelInfoService.findLevelByLevelId(user.getLevelId());
            userDto.setLevelTitle(level.getName());
        }


        final String getUserSuccessfully = generalMessageAccessor.getMessage(null, "get_user_successful", userId);

        return new GetUserProfileResponse(userDto, getUserSuccessfully);
    }

    @Transactional
    @Override
    public void deleteAccount(DeleteAccountRequest deleteAccountRequest, String authorizationHeader) {

        final UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        User user = userRepository.findByUserId(userId).orElse(null);

        if (user == null) {
            final String userNotFound = exceptionMessageAccessor.getMessage(null, "user_not_found");
            throw new BadRequestException(userNotFound);
        }

        otpService.deleteOtp(user.getUsername());

        userRepository.delete(user);

        redisService.storeAccessToken(deleteAccountRequest.getAccessToken());
        redisService.storeRefreshToken(deleteAccountRequest.getRefreshToken());
    }

    @Override
    public UUID getLevelId(UUID userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new UserNotFoundException("User not found with id " + userId));
        UUID levelId = user.getLevelId();
        if (levelStatusService.isLevelInStatus(levelId, ContentStatus.VERIFIED)) {
            return levelId;
        }
        throw new BadRequestException("User's level is not in VERIFIED status.");
    }

    @Override
    public void updateLevel(UUID levelId) {
        LevelDto newLevel = levelInfoService.findAvailableLevel();
        userRepository.updateAllByLevelId(levelId, newLevel.getLevelId());
    }
}