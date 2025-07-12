package com.hcmus.mela.user.strategy;

import com.hcmus.mela.user.dto.dto.UserDetailDto;
import com.hcmus.mela.user.exception.UserException;
import com.hcmus.mela.user.exception.UserNotFoundException;
import com.hcmus.mela.user.mapper.UserMapper;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.model.UserRole;
import com.hcmus.mela.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("USER_CONTRIBUTOR")
@RequiredArgsConstructor
public class UserFilterForContributorStrategy implements UserFilterStrategy {

    private final UserRepository userRepository;

    @Override
    public List<UserDetailDto> getUsers(UUID ownUserId, UserRole roleToGet) {
        if (roleToGet != null && roleToGet != UserRole.USER) {
            throw new UserException("Only USER role can be retrieved by contributor");
        }
        User user = userRepository.findById(ownUserId)
                .orElseThrow(() -> new UserException("User not found with id " + ownUserId));
        UUID levelId = user.getLevelId();
        List<User> users;
        if (levelId == null) {
            users = userRepository.findAllByUserRole(UserRole.USER);
        } else {
            users = userRepository.findAllByUserRoleAndLevelId(UserRole.USER, levelId);
        }
        return users.stream()
                .map(UserMapper.INSTANCE::userToUserDetailDto)
                .toList();
    }

    @Override
    public UserDetailDto getUserInfo(UUID ownUserId, UUID userIdToGet) {
        User ownUser = userRepository.findById(ownUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + ownUserId));
        UUID levelId = ownUser.getLevelId();
        User user = userRepository.findById(userIdToGet)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userIdToGet));
        if (user.getUserRole() != UserRole.USER) {
            throw new UserException("Only USER role can be retrieved by contributor");
        }
        if (levelId != null && !levelId.equals(user.getLevelId())) {
            throw new UserException("You can only view users in your level");
        }
        return UserMapper.INSTANCE.userToUserDetailDto(user);
    }
}
