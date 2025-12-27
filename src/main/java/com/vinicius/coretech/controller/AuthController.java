package com.vinicius.coretech.controller;

import com.vinicius.coretech.DTO.Request.LoginUserRequest;
import com.vinicius.coretech.DTO.Request.RegisterUserRequest;
import com.vinicius.coretech.DTO.Response.AuthUserResponse;
import com.vinicius.coretech.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterUserRequest registerUserRequest) {
        return authService.register(registerUserRequest.firstName(), registerUserRequest.lastName(), registerUserRequest.email(), registerUserRequest.password());
    }

    @PostMapping("/login")
    public AuthUserResponse login(@RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response) {
        return authService.login(loginUserRequest.email(), loginUserRequest.password(), response);
    }

    @PostMapping("/refresh-token")
    public Map<String, String> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        return authService.refresh(refreshToken, response);
    }

    @PostMapping("/logout")
    public String logout(@CookieValue("refreshToken") String refreshToken) {
        return authService.logout(refreshToken);
    }

    @GetMapping("/user-test")
    @PreAuthorize("hasRole('USER')")
    public String testUserAuth() {
        return "You are an USER.";
    }

    @GetMapping("/admin-test")
    @PreAuthorize("hasRole('ADMIN')")
    public String testAdminAuth() {
        return "You are an ADMIN.";
    }
}
