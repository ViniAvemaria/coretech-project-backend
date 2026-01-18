package com.vinicius.coretech.controller;

import com.vinicius.coretech.dto.Request.LoginUserRequest;
import com.vinicius.coretech.dto.Request.RegisterUserRequest;
import com.vinicius.coretech.exception.BadRequestException;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.frontend-base-url}")
    private String frontendUrl;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterUserRequest registerUserRequest) {
        authService.register(registerUserRequest.firstName(), registerUserRequest.lastName(), registerUserRequest.email(), registerUserRequest.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<Void> confirmEmail(@RequestParam String token, @RequestParam Long id) {
        String status;

        try {
            authService.confirmEmail(token, id);
            status="confirmation-success";
        } catch (BadRequestException e) {
            status="confirmation-failure";
        } catch (ResourceNotFoundException e) {
            status="not-found";
        }

        return ResponseEntity.status(302)
                .header("Location",
                        frontendUrl + "/account-status?status=" + status)
                .build();
    }

    @GetMapping("/resend-confirmation")
    public ResponseEntity<Void> resendConfirmation(@RequestParam String token, @RequestParam Long id) {
        String status;

        try {
            authService.resendConfirmation(token, id);
            status="resend-success";
        } catch (ConflictException e) {
            status="resend-failure";
        } catch (ResourceNotFoundException e) {
            status="not-found";
        }

        return ResponseEntity.status(302)
                .header("Location",
                        frontendUrl + "/account-status?status=" + status)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response) {
        authService.login(loginUserRequest.email(), loginUserRequest.password(), response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Void> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token cookie is missing");
        }

        authService.refresh(refreshToken, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token cookie is missing");
        }

        authService.logout(refreshToken, response);
        return ResponseEntity.noContent().build();
    }
}
