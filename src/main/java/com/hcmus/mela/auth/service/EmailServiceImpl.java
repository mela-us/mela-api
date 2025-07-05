package com.hcmus.mela.auth.service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.hcmus.mela.auth.exception.ForgotPasswordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${azure.email.sender-address}")
    private String fromEmail;
    private final EmailClient emailClient;
    private final TemplateEngine emailTemplateEngine;

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            Context context = new Context();
            context.setVariable("otpCode", otp);
            context.setVariable("currentYear", ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).getYear());
            context.setVariable("expirationMinutes", 5);
            String htmlContent = emailTemplateEngine.process("email-otp", context);

            EmailMessage emailMessage = new EmailMessage()
                    .setSenderAddress(fromEmail)
                    .setToRecipients(List.of(new EmailAddress(to)))
                    .setSubject("🔐 Mã OTP cho reset password")
                    .setBodyHtml(htmlContent);

            EmailSendResult result = emailClient.beginSend(emailMessage)
                    .waitForCompletion(Duration.ofMinutes(2))
                    .getValue();
            if (result.getStatus() == EmailSendStatus.SUCCEEDED) {
                log.info("OTP email sent successfully to {}", to);
            } else {
                throw new ForgotPasswordException("Failed to send OTP email with status " + result.getStatus());
            }
        } catch (Exception e) {
            throw new ForgotPasswordException("Failed to send OTP email, " + e.getMessage());
        }
    }
}