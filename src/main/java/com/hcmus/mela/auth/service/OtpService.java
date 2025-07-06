package com.hcmus.mela.auth.service;

import com.hcmus.mela.auth.model.User;

import java.util.UUID;

public interface OtpService {

    void generateAndSendOtp(String email);

    boolean validateOtp(String email, String inputOtp);

    void deleteOtp(String email);
}
