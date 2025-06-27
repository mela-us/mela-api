package com.hcmus.mela.skills.mapper;

import com.hcmus.mela.skills.dto.UserSkillDto;
import com.hcmus.mela.skills.model.UserSkill;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillMapper {
    UserSkillMapper INSTANCE = Mappers.getMapper(UserSkillMapper.class);

    UserSkillDto userSkillToUserSkillDto(UserSkill userSkill);
}
