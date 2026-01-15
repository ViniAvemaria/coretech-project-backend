package com.vinicius.coretech.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.backend-base-url}")
    private String backendUrl;

    @Value("${email.sender.from}")
    private String from;

    @Async
    public void sendConfirmationToken(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject("Account confirmation");
        message.setText("Welcome! Please activate your account by clicking the link below:\n\n" +
                backendUrl +"/api/auth/confirm-email?token=" + token +
                "\n\nThis link will expire in 24 hours.");
        mailSender.send(message);
    }
}
