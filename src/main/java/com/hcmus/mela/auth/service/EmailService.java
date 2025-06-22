package com.hcmus.mela.auth.service;

import com.hcmus.mela.auth.dto.dto.EmailDetailsDto;

public interface EmailService {

    String generateOtpNotify(String username, String otpCode);

    void sendSimpleMail(EmailDetailsDto details);
}
