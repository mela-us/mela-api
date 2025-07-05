package com.hcmus.mela.auth.service;

public interface EmailService {

    void sendOtpEmail(String to, String otp);
}
