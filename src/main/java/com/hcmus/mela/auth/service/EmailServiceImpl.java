package com.hcmus.mela.auth.service;

import com.hcmus.mela.auth.exception.ForgotPasswordException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine emailTemplateEngine;

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("otpCode", otp);
            context.setVariable("currentYear", ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).getYear());
            context.setVariable("expirationMinutes", 5);

            String htmlContent = emailTemplateEngine.process("email-otp", context);

            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject("🔐 Mã OTP cho reset password");
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("OTP email sent successfully to {}", to);

        } catch (Exception e) {
            throw new ForgotPasswordException("Failed to send OTP email, " + e.getMessage());
        }
    }
}
