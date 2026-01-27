package com.vinicius.coretech.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.backend-base-url}")
    private String backendUrl;

    @Value("${email.sender.from}")
    private String from;

    @Value("${MAIL_API}")
    private String mailApiToken;

    private WebClient client() {
        return webClientBuilder
                .baseUrl("https://send.api.mailtrap.io/api/send")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + mailApiToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Async
    public void sendConfirmationToken(String to, String token, Long id) {
        sendEmail(
                to,
                "Account confirmation",
                "Welcome! Please activate your account by clicking the link below:\n\n" +
                        backendUrl + "/api/auth/confirm-email?token=" + token + "&id=" + id +
                        "\n\nThis link will expire in 24 hours." +
                        "\n\nIf your link expires, request a new one here:\n" +
                        backendUrl + "/api/auth/resend-confirmation?token=" + token + "&id=" + id
        );
    }

    @Async
    public void sendRecoveryToken(String to, String token, Long id) {
        sendEmail(
                to,
                "Password Recovery",
                "You requested to reset your password.\n\n" +
                        "Click the link below to create a new password:\n\n" +
                        backendUrl + "/api/auth/validate-recovery-token?token=" + token + "&id=" + id +
                        "\n\nThis link will expire in 15 minutes.\n\n" +
                        "If you did not request this, you can safely ignore this email."
        );
    }

    @Async
    public void sendChangeEmailToken(String to, String token) {
        sendEmail(
                to,
                "Confirm Email Change",
                "You requested to change your email address.\n\n" +
                        "Click the link below to confirm this change:\n\n" + token +
                        "\n\nThis link will expire in 5 minutes.\n\n" +
                        "If you did not request this, you can safely ignore this email."
        );
    }

    @Async
    public void sendChangePasswordToken(String to, String token) {
        sendEmail(
                to,
                "Confirm Password Change",
                "You requested to change your password.\n\n" +
                        "Click the link below to confirm this change:\n\n" + token +
                        "\n\nThis link will expire in 5 minutes.\n\n" +
                        "If you did not request this, you can safely ignore this email."
        );
    }

    private void sendEmail(String to, String subject, String text) {
        client()
                .post()
                .bodyValue(Map.of(
                        "from", Map.of("email", from),
                        "to", new Object[]{Map.of("email", to)},
                        "subject", subject,
                        "text", text
                ))
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }
}
