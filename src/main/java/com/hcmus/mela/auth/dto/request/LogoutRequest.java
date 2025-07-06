package com.hcmus.mela.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogoutRequest {

    @NotEmpty(message = "Access token must not be empty")
    @NotNull(message = "Access token must not be null")
    private String accessToken;

    @NotEmpty(message = "Refresh token must not be empty")
    @NotNull(message = "Refresh token must not be null")
    private String refreshToken;
}
