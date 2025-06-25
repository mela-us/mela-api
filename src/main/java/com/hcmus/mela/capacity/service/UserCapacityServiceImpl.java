package com.hcmus.mela.capacity.service;

import com.hcmus.mela.capacity.dto.UserCapacityDto;
import com.hcmus.mela.capacity.dto.response.GetUserCapacityResponse;
import com.hcmus.mela.capacity.mapper.UserCapacityMapper;
import com.hcmus.mela.capacity.model.UserCapacity;
import com.hcmus.mela.capacity.repository.UserCapacityRepository;
import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.model.Level;
import com.hcmus.mela.lecture.model.Topic;
import com.hcmus.mela.lecture.service.LevelService;
import com.hcmus.mela.lecture.service.TopicService;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.user.dto.response.GetUserProfileResponse;
import com.hcmus.mela.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCapacityServiceImpl implements UserCapacityService {

    private final UserCapacityRepository userCapacityRepository;
    private final TopicService topicService;
    private final GeneralMessageAccessor generalMessageAccessor;

    private final String GET_USER_CAPACITY = "user_capacity_found";
    private final UserService userService;
    private final LevelService levelService;


    @Override
    public GetUserCapacityResponse getUserCapacity(String authorizationHeader) {
        GetUserProfileResponse response = userService.getUserProfile(authorizationHeader);

        UUID userId = response.getUser().getUserId();

        Level level = levelService.findLevelByLevelTitle(response.getUser().getLevelTitle());

        List<UserCapacity> userCapacities = checkUserCapacity(userId, level.getLevelId());

        List<UserCapacityDto> userCapacityDtos = userCapacities.stream()
                .map(userCapacity -> {
                    // Map basic fields
                    UserCapacityDto dto = UserCapacityMapper.INSTANCE.userCapacityToUserCapacityDto(userCapacity);

                    // Fetch topic and set topicName
                    TopicDto topic = topicService.getTopicById(userCapacity.getTopicId());
                    if (topic != null) {
                        dto.setTopicName(topic.getName());
                    } else {
                        dto.setTopicName(null); // or "Unknown"
                    }
                    return dto;
                }).toList();

        String getUserCapacitySucessMessage = generalMessageAccessor.getMessage(null, GET_USER_CAPACITY, userId);

        return new GetUserCapacityResponse(getUserCapacitySucessMessage, userCapacityDtos);
    }

    @Override
    public List<UserCapacity> checkUserCapacity(UUID userId, UUID levelId) {
        List<UserCapacity> userCapacities = userCapacityRepository.findAllByUserIdAndLevelId(userId, levelId);

        if (userCapacities.isEmpty()) {
            List<TopicDto> topics = topicService.getTopics();

            for (TopicDto topic : topics) {
                UserCapacity userCapacity = UserCapacity.builder()
                        .userCapacityId(UUID.randomUUID())
                        .userId(userId)
                        .levelId(levelId)
                        .topicId(topic.getTopicId())
                        .excellence(0.0)
                        .build();

                UserCapacity savedUserCapacity = userCapacityRepository.save(userCapacity);
                userCapacities.add(savedUserCapacity);
            }
        }

        return userCapacities;
    }

    @Override
    public void updateUserCapacity(UUID userId, UUID levelId, UUID topicId, Integer correctAnswers, Integer incorrectAnswers) {
        UserCapacity userCapacity = userCapacityRepository.findByUserIdAndLevelIdAndTopicId(userId, levelId, topicId);

        if (userCapacity == null) {
            userCapacity = UserCapacity.builder()
                    .userCapacityId(UUID.randomUUID())
                    .userId(userId)
                    .levelId(levelId)
                    .topicId(topicId)
                    .excellence(0.0)
                    .build();
        }

        userCapacity.setExcellence(correctAnswers.doubleValue() - incorrectAnswers.doubleValue());

        userCapacityRepository.updateUserCapacity(userCapacity);

    }
}
