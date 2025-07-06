package com.hcmus.mela.user.dto.response;

import com.hcmus.mela.user.dto.dto.UserDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserDetailResponse {

    private String message;

    private UserDetailDto data;
}
