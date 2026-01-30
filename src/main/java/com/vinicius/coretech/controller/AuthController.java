package com.vinicius.coretech.controller;

import com.vinicius.coretech.dto.Request.LoginUserRequest;
import com.vinicius.coretech.dto.Request.RecoverPasswordRequest;
import com.vinicius.coretech.dto.Request.RegisterUserRequest;
import com.vinicius.coretech.dto.Request.ResetPasswordRequest;
import com.vinicius.coretech.dto.Request.ValidationTokenRequest;
import com.vinicius.coretech.entity.TokenType;
import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.exception.BadRequestException;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.service.AuthService;
import com.vinicius.coretech.service.SecurityService;
import com.vinicius.coretech.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final SecurityService securityService;
    private final TokenService tokenService;

    @Value("${app.frontend-base-url}")
    private String frontendUrl;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        authService.register(registerUserRequest.firstName(), registerUserRequest.lastName(), registerUserRequest.email(), registerUserRequest.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<Void> confirmEmail(@NotBlank @RequestParam String token, @Min(1) @RequestParam Long id) {
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
    public ResponseEntity<Void> resendConfirmation(@NotBlank @RequestParam String token, @Min(1) @RequestParam Long id) {
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
    public ResponseEntity<Void> login(@Valid @RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response) {
        authService.login(loginUserRequest.email(), loginUserRequest.password(), response);
        return ResponseEntity.noContent().build();
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
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token cookie is missing");
        }

        authService.logout(refreshToken, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recover-password")
    public ResponseEntity<Void> recoverPassword(@Valid @RequestBody RecoverPasswordRequest request) {
        authService.recoverPassword(request.email());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate-recovery-token")
    public ResponseEntity<Void> validateRecoveryToken(@NotBlank @RequestParam String token, @Min(1) @RequestParam Long id) {
        String status;

        try {
            tokenService.validateRecoveryToken(token, id);
            status = "success";
        } catch(BadRequestException e) {
            status="failure";
        } catch(ResourceNotFoundException e) {
            status="not-found";
        }

        return ResponseEntity.status(302)
                .header("Location",
                        frontendUrl + "/reset-password?status=" + status + "&token=" + token + "&id=" + id)
                .build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@NotBlank @RequestParam String token, @Min(1) @RequestParam Long id, @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(token, id, request.password());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/change-email")
    public ResponseEntity<Void> changeEmail() {
        authService.changeEmail();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/change-password")
    public ResponseEntity<Void> changePassword() {
        authService.changePassword();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate-email-change")
    public ResponseEntity<Void> validateEmailChange(@Valid @RequestBody ValidationTokenRequest request) {
        User user = securityService.getUserFromSecurityContext();
        tokenService.validateEmailAndPasswordChange(request.token(), TokenType.CHANGE_EMAIL, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate-password-change")
    public ResponseEntity<Void> validatePasswordChange(@Valid @RequestBody ValidationTokenRequest request) {
        User user = securityService.getUserFromSecurityContext();
        tokenService.validateEmailAndPasswordChange(request.token(), TokenType.CHANGE_PASSWORD, user);
        return ResponseEntity.noContent().build();
    }
}
