package com.hcmus.mela.capacity.mapper;

import com.hcmus.mela.capacity.dto.UserCapacityDto;
import com.hcmus.mela.capacity.model.UserCapacity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserCapacityMapper {
    UserCapacityMapper INSTANCE = Mappers.getMapper(UserCapacityMapper.class);

    UserCapacityDto userCapacityToUserCapacityDto(UserCapacity userCapacity);
}
