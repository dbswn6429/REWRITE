package com.example.rewrite.service.mail;

public interface MailService {
    void sendUserIdByEmail(String toEmail, String userId);
    void sendUserIdPasswordByEmail(String toEmail, String userId, String password);
    void sendWelcomeEmail(String toEmail, String userId, String name);
}
