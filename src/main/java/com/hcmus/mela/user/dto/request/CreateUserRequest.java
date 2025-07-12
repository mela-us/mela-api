package com.hcmus.mela.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hcmus.mela.user.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {

    @NotNull(message = "Username cannot be null")
    @Email(message = "Username must be a valid email address")
    private String username;

    @NotNull(message = "Password cannot be null")
    private String password;

    private String fullname;

    private String imageUrl;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date birthday;

    @NotNull(message = "Level id cannot be null")
    private UUID levelId;

    @NotNull(message = "Role cannot be null")
    private UserRole userRole;
}
