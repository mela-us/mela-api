package com.hcmus.mela.auth.service;

import com.hcmus.mela.auth.dto.request.LoginRequest;
import com.hcmus.mela.auth.dto.request.LogoutRequest;
import com.hcmus.mela.auth.dto.request.RefreshTokenRequest;
import com.hcmus.mela.auth.dto.request.RegistrationRequest;
import com.hcmus.mela.auth.dto.response.LoginResponse;
import com.hcmus.mela.auth.dto.response.LogoutResponse;
import com.hcmus.mela.auth.dto.response.RefreshTokenResponse;
import com.hcmus.mela.auth.dto.response.RegistrationResponse;
import com.hcmus.mela.auth.exception.InvalidTokenException;
import com.hcmus.mela.auth.exception.RegistrationException;
import com.hcmus.mela.auth.mapper.UserMapper;
import com.hcmus.mela.auth.model.User;
import com.hcmus.mela.auth.model.UserRole;
import com.hcmus.mela.auth.repository.AuthRepository;
import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.shared.cache.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RedisService redisService;

    @Override
    public RegistrationResponse getRegistrationResponse(RegistrationRequest registrationRequest) {
        final String username = registrationRequest.getUsername();
        final boolean existsByUsername = authRepository.existsByUsername(username);

        if (existsByUsername) {
            log.warn("{} is already being used!", username);
            String existsUsernameMsg = "This username is already being used!";
            throw new RegistrationException(existsUsernameMsg);
        }
        final User user = UserMapper.INSTANCE.registrationRequestToUser(registrationRequest);
        user.setUserId(UUID.randomUUID());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setUserRole(UserRole.USER);
        user.setCreatedAt(Date.from(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant()));
        user.setUpdatedAt(user.getCreatedAt());
        authRepository.save(user);

        final String registrationSuccessMessage = String.format("%s registered successfully!", username);
        return new RegistrationResponse(registrationSuccessMessage);
    }

    @Override
    public RefreshTokenResponse getRefreshTokenResponse(RefreshTokenRequest refreshTokenRequest) {
        final String refreshToken = refreshTokenRequest.getRefreshToken();
        // Check if the refresh token is blacklisted
        if (redisService.isRefreshTokenBlacklisted(refreshToken)) {
            throw new InvalidTokenException("Refresh token is in blacklist!");
        }
        final boolean validToken = jwtTokenService.validateToken(refreshToken);
        final String username = jwtTokenService.getUsernameFromToken(refreshToken);
        final User user = authRepository.findByUsername(username);
        if (validToken) {
            return new RefreshTokenResponse(jwtTokenService.generateAccessToken(user));
        } else {
            throw new InvalidTokenException("Token is invalid or expired!");
        }
    }


    public LoginResponse getLoginResponse(LoginRequest loginRequest) {
        final String username = loginRequest.getUsername();
        final String password = loginRequest.getPassword();
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(authenticationToken);
        final User user = authRepository.findByUsername(username);
        final String accessToken = jwtTokenService.generateAccessToken(user);
        final String refreshToken = jwtTokenService.generateRefreshToken(user);
        final String role = user.getUserRole().toString();

        return new LoginResponse(accessToken, refreshToken, role);
    }

    @Override
    public LogoutResponse getLogoutResponse(LogoutRequest logoutRequest) {
        redisService.storeAccessToken(logoutRequest.getAccessToken());
        redisService.storeRefreshToken(logoutRequest.getRefreshToken());
        return new LogoutResponse("Logout successfully!");
    }

    @Override
    public User findByUsername(String username) {
        return authRepository.findByUsername(username);
    }

    @Override
    public void updatePassword(String username, String newPassword) {
        User user = this.findByUsername(username);
        if (user != null && newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            user.setUpdatedAt(Date.from(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant()));
            authRepository.save(user);
        }
    }
}
