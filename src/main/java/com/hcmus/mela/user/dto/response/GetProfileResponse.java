package com.hcmus.mela.user.dto.response;

import com.hcmus.mela.user.dto.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProfileResponse {

    private UserDto user;

    private String message;
}
