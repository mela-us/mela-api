package com.hcmus.mela.user.mapper;


import com.hcmus.mela.user.dto.dto.UserDetailDto;
import com.hcmus.mela.user.dto.dto.UserDto;
import com.hcmus.mela.user.dto.dto.UserPreviewDto;
import com.hcmus.mela.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    UserDetailDto userToUserDetailDto(User user);

    UserPreviewDto userToUserPreviewDto(User user);
}
