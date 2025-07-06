package com.hcmus.mela.auth.service;

import com.hcmus.mela.auth.dto.request.LoginRequest;
import com.hcmus.mela.auth.dto.request.LogoutRequest;
import com.hcmus.mela.auth.dto.request.RefreshTokenRequest;
import com.hcmus.mela.auth.dto.request.RegistrationRequest;
import com.hcmus.mela.auth.dto.response.LoginResponse;
import com.hcmus.mela.auth.dto.response.LogoutResponse;
import com.hcmus.mela.auth.dto.response.RefreshTokenResponse;
import com.hcmus.mela.auth.dto.response.RegistrationResponse;
import com.hcmus.mela.auth.model.User;

public interface AuthService {

    RegistrationResponse getRegistrationResponse(RegistrationRequest registrationRequest);

    RefreshTokenResponse getRefreshTokenResponse(RefreshTokenRequest refreshTokenRequest);

    LoginResponse getLoginResponse(LoginRequest loginRequest);

    LogoutResponse getLogoutResponse(LogoutRequest logoutRequest);

    User findByUsername(String username);

    void updatePassword(String username, String newPassword);
}
