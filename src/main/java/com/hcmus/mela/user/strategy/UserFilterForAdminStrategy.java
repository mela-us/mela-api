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

@Component("USER_ADMIN")
@RequiredArgsConstructor
public class UserFilterForAdminStrategy implements UserFilterStrategy {

    private final UserRepository userRepository;

    @Override
    public List<UserDetailDto> getUsers(UUID ownUserId, UserRole roleToGet) {
        List<User> users = null;
        if (roleToGet == null) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findAllByUserRole(roleToGet);
        }
        return users.stream()
                .filter(user -> user.getUserRole() != UserRole.ADMIN)
                .map(UserMapper.INSTANCE::userToUserDetailDto)
                .toList();
    }

    @Override
    public UserDetailDto getUserInfo(UUID ownUserId, UUID userIdToGet) {
        User user = userRepository.findByUserId(userIdToGet)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userIdToGet));
        if (user.getUserRole() == UserRole.ADMIN) {
            throw new UserException("Cannot get admin user information");
        }
        return UserMapper.INSTANCE.userToUserDetailDto(user);
    }
}