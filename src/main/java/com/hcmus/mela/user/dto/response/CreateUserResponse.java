package com.hcmus.mela.user.dto.response;

import com.hcmus.mela.user.dto.dto.UserDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateUserResponse {

    private String message;

    private UserDetailDto data;
}
