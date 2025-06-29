package com.hcmus.mela.skills.service;

import com.hcmus.mela.skills.dto.response.GetUserSkillResponse;
import com.hcmus.mela.skills.model.UserSkill;

import java.util.List;
import java.util.UUID;

public interface UserSkillService {
    GetUserSkillResponse getUserSkill(String authorizationHeader);

    List<UserSkill> checkUserSkill(UUID userId, UUID levelId);

    void updateUserSkill(UUID userId, UUID levelId, UUID topicId, Integer correctAnswers, Integer incorrectAnswers);
}
