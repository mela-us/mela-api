package com.hcmus.mela.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hcmus.mela.user.model.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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

    private String levelId;

    private UserRole role;
}
