package com.hcmus.mela.user.service;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.service.LevelInfoService;
import com.hcmus.mela.level.service.LevelStatusService;
import com.hcmus.mela.shared.exception.BadRequestException;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.user.dto.dto.UserDto;
import com.hcmus.mela.user.exception.UserNotFoundException;
import com.hcmus.mela.user.mapper.UserMapper;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserRepository userRepository;
    private final LevelStatusService levelStatusService;
    private final LevelInfoService levelInfoService;

    @Override
    public User getUserByUserId(UUID userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
    }

    @Override
    public UserDto getUserDtoByUserId(UUID userId) {
        User user = getUserByUserId(userId);
        return UserMapper.INSTANCE.userToUserDto(user);
    }

    @Override
    public UUID getLevelIdOfUser(UUID userId) {
        User user = getUserByUserId(userId);
        UUID levelId = user.getLevelId();
        if (levelStatusService.isLevelInStatus(levelId, ContentStatus.VERIFIED)) {
            return levelId;
        }
        throw new BadRequestException("User's level is not in VERIFIED status.");
    }

    @Override
    public void updateLevelForAllUser(UUID oldLevelId) {
        LevelDto newLevel = levelInfoService.findAvailableLevel();
        userRepository.updateAllByLevelId(oldLevelId, newLevel.getLevelId());
    }
}
