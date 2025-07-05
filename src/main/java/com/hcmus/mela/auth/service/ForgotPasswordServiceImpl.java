package com.hcmus.mela.auth.service;

import com.hcmus.mela.auth.dto.request.ForgotPasswordRequest;
import com.hcmus.mela.auth.dto.request.OtpConfirmationRequest;
import com.hcmus.mela.auth.dto.request.ResetPasswordRequest;
import com.hcmus.mela.auth.dto.response.ForgotPasswordResponse;
import com.hcmus.mela.auth.dto.response.OtpConfirmationResponse;
import com.hcmus.mela.auth.dto.response.ResetPasswordResponse;
import com.hcmus.mela.auth.exception.ForgotPasswordException;
import com.hcmus.mela.auth.exception.InvalidTokenException;
import com.hcmus.mela.auth.exception.UserNotFoundException;
import com.hcmus.mela.auth.model.User;
import com.hcmus.mela.auth.security.jwt.JwtTokenForgotPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final AuthService authService;
    private final OtpService otpService;
    private final JwtTokenForgotPasswordService jwtTokenForgotPasswordService;

    @Override
    public ForgotPasswordResponse sendOtpCodeByEmail(ForgotPasswordRequest request) {
        User user = authService.findByUsername(request.getUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found with username: " + request.getUsername());
        }
        otpService.generateAndSendOtp(user.getUsername());
        return new ForgotPasswordResponse("Send email successfully to " + user.getUsername());
    }

    @Override
    public OtpConfirmationResponse validateOtp(OtpConfirmationRequest request) {
        User user = authService.findByUsername(request.getUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found with username: " + request.getUsername());
        }
        if (!otpService.validateOtp(request.getUsername(), request.getOtpCode())) {
            throw new ForgotPasswordException("Invalid OTP code for user " + request.getUsername());
        }
        String token = jwtTokenForgotPasswordService.generateToken(request.getUsername());
        OtpConfirmationResponse otpConfirmationResponse = new OtpConfirmationResponse();
        otpConfirmationResponse.setUsername(request.getUsername());
        otpConfirmationResponse.setToken(token);
        otpConfirmationResponse.setMessage("OTP code is valid. You can reset your password now.");
        otpService.deleteOtp(request.getUsername());
        return otpConfirmationResponse;
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        if (jwtTokenForgotPasswordService.validateToken(request.getToken(), request.getUsername())) {
            authService.updatePassword(request.getUsername(), request.getNewPassword());
            return new ResetPasswordResponse("Password reset successfully for user " + request.getUsername());
        }
        throw new InvalidTokenException("Invalid token for user " + request.getUsername() + " to reset password.");
    }
}
