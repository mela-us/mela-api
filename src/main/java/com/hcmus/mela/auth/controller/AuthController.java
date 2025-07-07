package com.hcmus.mela.auth.controller;

import com.hcmus.mela.auth.dto.request.LoginRequest;
import com.hcmus.mela.auth.dto.request.LogoutRequest;
import com.hcmus.mela.auth.dto.request.RefreshTokenRequest;
import com.hcmus.mela.auth.dto.request.RegistrationRequest;
import com.hcmus.mela.auth.dto.response.LoginResponse;
import com.hcmus.mela.auth.dto.response.LogoutResponse;
import com.hcmus.mela.auth.dto.response.RefreshTokenResponse;
import com.hcmus.mela.auth.dto.response.RegistrationResponse;
import com.hcmus.mela.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(tags = "🔐 Auth Service", summary = "Login",
            description = "You must log in with the correct information to successfully obtain the token information.")
    public ResponseEntity<LoginResponse> loginRequest(@Valid @RequestBody LoginRequest request) {
        log.info("Login with {}", request.getUsername());
        final LoginResponse response = authService.getLoginResponse(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(tags = "🔐 Auth Service", summary = "Register",
            description = "You can register to the system by sending information in the appropriate format.")
    public ResponseEntity<RegistrationResponse> registerRequest(@Valid @RequestBody RegistrationRequest request) {
        log.info("Register with {}", request.getUsername());
        final RegistrationResponse response = authService.getRegistrationResponse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh-token")
    @Operation(tags = "🔐 Auth Service", summary = "Refresh token",
            description = "API endpoint to refresh access token using the provided refresh token.")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token for user with access token {}", request.getRefreshToken().substring(0, 10));
        final RefreshTokenResponse response = authService.getRefreshTokenResponse(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/logout")
    @Operation(tags = "🔐 Auth Service", summary = "Logout",
            description = "API endpoint to logout.")
    public ResponseEntity<LogoutResponse> logout(@Valid @RequestBody LogoutRequest request) {
        log.info("Logout for user with access token {}", request.getAccessToken().substring(0, 10));
        final LogoutResponse response = authService.getLogoutResponse(request);
        return ResponseEntity.ok(response);
    }
}
