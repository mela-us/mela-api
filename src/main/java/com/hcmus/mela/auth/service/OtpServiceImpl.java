package com.hcmus.mela.auth.service;

import com.hcmus.mela.shared.cache.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private final RedisService redisService;
    private final EmailService emailService;

    private String generateRandomOtp() {
        SecureRandom secureRandom = new SecureRandom();
        int otpInt = secureRandom.nextInt(1_000_000);
        return String.format("%06d", otpInt);
    }


    @Override
    public void generateAndSendOtp(String email) {
        String otp = generateRandomOtp();
        redisService.storeResetPasswordOtp(email, otp, OTP_EXPIRY_MINUTES);
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public boolean validateOtp(String email, String inputOtp) {
        return redisService.validateResetPasswordOtp(email, inputOtp);
    }

    @Override
    public void deleteOtp(String email) {
        redisService.removeResetPasswordOtp(email);
    }
}
