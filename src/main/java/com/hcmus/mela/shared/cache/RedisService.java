package com.hcmus.mela.shared.cache;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final JwtTokenService jwtTokenService;

    // Store the access token
    public void storeAccessToken(String token) {
        long duration = jwtTokenService.getRemainingTimeBeforeExpiration(token);
        redisTemplate.opsForValue().set("access_token:" + token, "blacklisted", Duration.ofSeconds(duration));
    }

    // Store the refresh token
    public void storeRefreshToken(String token) {
        long duration = jwtTokenService.getRemainingTimeBeforeExpiration(token);
        redisTemplate.opsForValue().set("refresh_token:" + token, "blacklisted", Duration.ofSeconds(duration));
    }

    // Check if the access token is blacklisted
    public boolean isAccessTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("access_token:" + token));
    }

    // Check if the refresh token is blacklisted
    public boolean isRefreshTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("refresh_token:" + token));
    }

    // Remove the access token from Redis
    public void removeAccessToken(String token) {
        redisTemplate.delete("access_token:" + token);
    }

    // Remove the refresh token from Redis
    public void removeRefreshToken(String token) {
        redisTemplate.delete("refresh_token:" + token);
    }

    // Store the reset password OTP
    public void storeResetPasswordOtp(String email, String otp, int otpExpiryMinutes) {
        redisTemplate.opsForValue().set("email_otp:" + email, otp, Duration.ofMinutes(otpExpiryMinutes));
    }

    // Validate the reset password OTP
    public boolean validateResetPasswordOtp(String email, String otp) {
        String storedOtp = (String) redisTemplate.opsForValue().get("email_otp:" + email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete("email_otp:" + email);
            return true;
        }
        return false;
    }

    // Remove the reset password OTP
    public void removeResetPasswordOtp(String email) {
        redisTemplate.delete("email_otp:" + email);
    }
}
