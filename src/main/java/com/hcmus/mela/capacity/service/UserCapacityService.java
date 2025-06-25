package com.hcmus.mela.capacity.service;

import com.hcmus.mela.capacity.dto.response.GetUserCapacityResponse;
import com.hcmus.mela.capacity.model.UserCapacity;

import java.util.List;
import java.util.UUID;

public interface UserCapacityService {
    GetUserCapacityResponse getUserCapacity(String authorizationHeader);

    List<UserCapacity> checkUserCapacity(UUID userId, UUID levelId);

    void updateUserCapacity(UUID userId, UUID levelId, UUID topicId, Integer correctAnswers, Integer incorrectAnswers);
}
