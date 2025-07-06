package com.hcmus.mela.auth.controller;

import com.hcmus.mela.auth.dto.request.ForgotPasswordRequest;
import com.hcmus.mela.auth.dto.request.OtpConfirmationRequest;
import com.hcmus.mela.auth.dto.request.ResetPasswordRequest;
import com.hcmus.mela.auth.dto.response.ForgotPasswordResponse;
import com.hcmus.mela.auth.dto.response.OtpConfirmationResponse;
import com.hcmus.mela.auth.dto.response.ResetPasswordResponse;
import com.hcmus.mela.auth.service.ForgotPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/forgot-password")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping
    @Operation(tags = "🔑 Forgot Password Service", summary = "Send email for forgot password",
            description = "You can enter your email to receive otp via the email.")
    public ResponseEntity<ForgotPasswordResponse> forgotPasswordRequest(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for email {}", request.getUsername());
        final ForgotPasswordResponse response = forgotPasswordService.sendOtpCodeByEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-otp")
    @Operation(tags = "🔑 Forgot Password Service", summary = "Validate OTP",
            description = "You must provide otp code to verify your account.")
    public ResponseEntity<OtpConfirmationResponse> validateOtpRequest(@Valid @RequestBody OtpConfirmationRequest request) {
        log.info("Validating OTP for email {}", request.getUsername());
        final OtpConfirmationResponse response = forgotPasswordService.validateOtp(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/reset-password")
    @Operation(tags = "🔑 Forgot Password Service", summary = "Reset Password",
            description = "You can reset your password.")
    public ResponseEntity<ResetPasswordResponse> resetPasswordRequest(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Resetting password for email {}", request.getUsername());
        final ResetPasswordResponse response = forgotPasswordService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}
