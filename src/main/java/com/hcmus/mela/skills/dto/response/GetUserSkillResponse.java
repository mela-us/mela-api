package com.hcmus.mela.skills.dto.response;

import com.hcmus.mela.skills.dto.dto.UserSkillDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserSkillResponse {

    private String message;

    private List<UserSkillDto> detailedStats;
}
