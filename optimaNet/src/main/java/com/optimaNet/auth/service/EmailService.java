package com.optimaNet.auth.service;

public interface EmailService {
    void sendOtp(String toEmail,String subject, String otp);
}
