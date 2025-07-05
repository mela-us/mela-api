package com.hcmus.mela.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ForgotPasswordRequest {

    @NotEmpty(message = "Username must not be empty")
    @Email(message = "Username must be a valid email")
    private String username;
}
