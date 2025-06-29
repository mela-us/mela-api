package com.hcmus.mela.skills.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillDto {
    private String topicName;
    private Double points;
}