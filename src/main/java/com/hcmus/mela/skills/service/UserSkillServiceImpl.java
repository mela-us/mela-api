package com.hcmus.mela.skills.service;

import com.hcmus.mela.level.service.LevelStatusService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.skills.dto.dto.UserSkillDto;
import com.hcmus.mela.skills.dto.response.GetUserSkillResponse;
import com.hcmus.mela.skills.mapper.UserSkillMapper;
import com.hcmus.mela.skills.model.UserSkill;
import com.hcmus.mela.skills.repository.UserSkillRepository;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.service.TopicInfoService;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSkillServiceImpl implements UserSkillService {

    private final String GET_USER_SKILL = "user_skill_found";

    private final GeneralMessageAccessor generalMessageAccessor;
    private final UserSkillRepository userSkillRepository;
    private final UserInfoService userInfoService;
    private final TopicInfoService topicInfoService;
    private final LevelStatusService levelStatusService;


    @Override
    public GetUserSkillResponse getUserSkillsByUserId(UUID userId) {
        User user = userInfoService.getUserById(userId);

        boolean isLevelVerified = levelStatusService.isLevelInStatus(user.getLevelId(), ContentStatus.VERIFIED);
        if (!isLevelVerified) {
            return new GetUserSkillResponse("Level of user is invalid", List.of());
        }

        List<UserSkill> userSkills = getUserSkillsByLevelId(userId, user.getLevelId());
        List<UserSkillDto> userSkillDtos = userSkills.stream()
                .map(userSkill -> {
                    UserSkillDto skill = UserSkillMapper.INSTANCE.userSkillToUserSkillDto(userSkill);
                    TopicDto topic = topicInfoService.findTopicByTopicId(userSkill.getTopicId());
                    if (topic != null) {
                        skill.setTopicName(topic.getName());
                    } else {
                        skill.setTopicName(null);
                    }
                    return skill;
                }).toList();

        String getUserSkillSuccessMessage = generalMessageAccessor.getMessage(null, GET_USER_SKILL, userId);
        return new GetUserSkillResponse(getUserSkillSuccessMessage, userSkillDtos);
    }

    @Override
    public List<UserSkill> getUserSkillsByLevelId(UUID userId, UUID levelId) {
        List<UserSkill> userSkills = userSkillRepository.findAllByUserIdAndLevelId(userId, levelId);
        List<UserSkill> validUserSkills = new ArrayList<>();
        List<TopicDto> topics = topicInfoService.findAllTopicsInStatus(ContentStatus.VERIFIED);
        for (TopicDto topic : topics) {
            UserSkill existingUserSkill = userSkills.stream()
                    .filter(us -> us.getUserId().equals(userId) && us.getLevelId().equals(levelId) && us.getTopicId().equals(topic.getTopicId()))
                    .findFirst()
                    .orElse(null);
            if (existingUserSkill == null) {
                UserSkill newUserSkill = UserSkill.builder()
                        .userSkillId(UUID.randomUUID())
                        .userId(userId)
                        .levelId(levelId)
                        .topicId(topic.getTopicId())
                        .points(0)
                        .build();
                existingUserSkill = userSkillRepository.save(newUserSkill);
            }
            validUserSkills.add(existingUserSkill);
        }
        return validUserSkills;
    }

    @Override
    public void updateUserSkill(UUID userId, UUID levelId, UUID topicId, Integer correctAnswers, Integer incorrectAnswers) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndLevelIdAndTopicId(userId, levelId, topicId).orElse(null);
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

    @Override
    public void deleteUserSkillByUserId(UUID userId) {
        List<UserSkill> userSkills = userSkillRepository.findAllByUserId(userId);
        if (userSkills.isEmpty()) {
            return;
        }
        userSkillRepository.deleteAll(userSkills);
    }
}
