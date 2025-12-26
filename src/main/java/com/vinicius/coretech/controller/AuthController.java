package com.vinicius.coretech.controller;

import com.vinicius.coretech.DTO.Request.LoginUserRequest;
import com.vinicius.coretech.DTO.Request.RegisterUserRequest;
import com.vinicius.coretech.DTO.Response.AuthUserResponse;
import com.vinicius.coretech.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public AuthUserResponse login(@RequestBody LoginUserRequest loginUserRequest) {
        return authService.login(loginUserRequest.email(), loginUserRequest.password());
    }

    @GetMapping("/user-test")
    @PreAuthorize("hasRole('USER')")
    public String testAuth() {
        return "You are an USER.";
    }

    @GetMapping("/admin-test")
    @PreAuthorize("hasRole('ADMIN')")
    public String testAdminAuth() {
        return "You are an ADMIN.";
    }
}
