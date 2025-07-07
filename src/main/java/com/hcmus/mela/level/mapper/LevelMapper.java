package com.hcmus.mela.level.mapper;

import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.dto.request.CreateLevelRequest;
import com.hcmus.mela.level.model.Level;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LevelMapper {

    LevelMapper INSTANCE = Mappers.getMapper(LevelMapper.class);

    LevelDto levelToLevelDto(Level level);

    Level createLevelRequestToLevel(CreateLevelRequest createLevelRequest);
}
