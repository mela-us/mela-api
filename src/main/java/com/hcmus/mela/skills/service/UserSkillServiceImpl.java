package com.hcmus.mela.skills.service;

import com.hcmus.mela.level.model.Level;
import com.hcmus.mela.level.service.LevelService;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.skills.dto.UserSkillDto;
import com.hcmus.mela.skills.dto.response.GetUserSkillResponse;
import com.hcmus.mela.skills.mapper.UserSkillMapper;
import com.hcmus.mela.skills.model.UserSkill;
import com.hcmus.mela.skills.repository.UserSkillRepository;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.service.TopicService;
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
public class UserSkillServiceImpl implements UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final TopicService topicService;
    private final GeneralMessageAccessor generalMessageAccessor;

    private final String GET_USER_SKILL = "user_skill_found";
    private final UserService userService;
    private final LevelService levelService;


    @Override
    public GetUserSkillResponse getUserSkill(String authorizationHeader) {
        GetUserProfileResponse response = userService.getUserProfile(authorizationHeader);

        UUID userId = response.getUser().getUserId();

        Level level = levelService.findLevelByLevelTitle(response.getUser().getLevelTitle());

        List<UserSkill> userSkills = checkUserSkill(userId, level.getLevelId());

        List<UserSkillDto> userSkillDtos = userSkills.stream()
                .map(userSkill -> {
                    // Map basic fields
                    UserSkillDto dto = UserSkillMapper.INSTANCE.userSkillToUserSkillDto(userSkill);

                    // Fetch topic and set topicName
                    TopicDto topic = topicService.getTopicById(userSkill.getTopicId());
                    if (topic != null) {
                        dto.setTopicName(topic.getName());
                    } else {
                        dto.setTopicName(null); // or "Unknown"
                    }
                    return dto;
                }).toList();

        String getUserSkillSuccessMessage = generalMessageAccessor.getMessage(null, GET_USER_SKILL, userId);

        return new GetUserSkillResponse(getUserSkillSuccessMessage, userSkillDtos);
    }

    @Override
    public List<UserSkill> checkUserSkill(UUID userId, UUID levelId) {
        List<UserSkill> userSkills = userSkillRepository.findAllByUserIdAndLevelId(userId, levelId);

        if (userSkills.isEmpty()) {
            List<TopicDto> topics = topicService.getTopics();

            for (TopicDto topic : topics) {
                UserSkill userSkill = UserSkill.builder()
                        .userSkillId(UUID.randomUUID())
                        .userId(userId)
                        .levelId(levelId)
                        .topicId(topic.getTopicId())
                        .points(0)
                        .build();

                UserSkill savedUserSkill = userSkillRepository.save(userSkill);
                userSkills.add(savedUserSkill);
            }
        }

        return userSkills;
    }

    @Override
    public void updateUserSkill(UUID userId, UUID levelId, UUID topicId, Integer correctAnswers, Integer incorrectAnswers) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndLevelIdAndTopicId(userId, levelId, topicId);

        if (userSkill == null) {
            userSkill = UserSkill.builder()
                    .userSkillId(UUID.randomUUID())
                    .userId(userId)
                    .levelId(levelId)
                    .topicId(topicId)
                    .points(0)
                    .build();
        }
        int bonusPoints = correctAnswers - incorrectAnswers;
        int points = Math.max(userSkill.getPoints() + bonusPoints, 0);
        userSkill.setPoints(points);

        userSkillRepository.updateUserSkill(userSkill);

    }
}
