package com.hcmus.mela.user.service;

import com.hcmus.mela.user.dto.UserDto;
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

    @Override
    public UUID getLevelOfUser(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        return user.getLevelId();
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
    }
}