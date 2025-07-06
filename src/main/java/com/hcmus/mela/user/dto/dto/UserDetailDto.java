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
public class UserDetailDto {

    private UUID userId;

    private String username;

    private String fullname;

    private Date birthday;

    private String imageUrl;

    private String levelId;

    private Date createdAt;

    private Date updatedAt;

    private UserRole userRole;
}
