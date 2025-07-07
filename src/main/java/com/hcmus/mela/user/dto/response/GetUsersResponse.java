package com.hcmus.mela.user.dto.response;

import com.hcmus.mela.user.dto.dto.UserDetailDto;
import com.hcmus.mela.user.dto.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUsersResponse {

    private String message;

    private List<UserDetailDto> data;
}
