package com.hcmus.mela.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hcmus.mela.user.model.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {

    private String username;

    private String password;

    private String fullname;

    private String imageUrl;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date birthday;

    private UUID levelId;

    private UserRole userRole;
}
