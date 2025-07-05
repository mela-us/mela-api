package com.hcmus.mela.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenRequest {

    @NotEmpty(message = "Refresh token must not be empty")
    private String refreshToken;
}
