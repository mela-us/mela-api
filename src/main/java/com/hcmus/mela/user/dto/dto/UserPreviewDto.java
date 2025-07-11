package com.hcmus.mela.user.dto.dto;

import com.hcmus.mela.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserPreviewDto {

    private String username;

    private String fullname;

    private UserRole userRole;
}
