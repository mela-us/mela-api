package com.hcmus.mela.service;

import com.hcmus.mela.security.dto.EmailDetails;

public interface EmailService {
    public String sendSimpleMail(EmailDetails details);
}
