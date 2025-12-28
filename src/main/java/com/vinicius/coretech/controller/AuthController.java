package com.vinicius.coretech.controller;

import com.vinicius.coretech.DTO.Request.LoginUserRequest;
import com.vinicius.coretech.DTO.Request.RegisterUserRequest;
import com.vinicius.coretech.DTO.Response.ApiResponse;
import com.vinicius.coretech.DTO.Response.AuthUserResponse;
import com.vinicius.coretech.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
    public ResponseEntity<Void> register(@RequestBody RegisterUserRequest registerUserRequest) {
        authService.register(registerUserRequest.firstName(), registerUserRequest.lastName(), registerUserRequest.email(), registerUserRequest.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthUserResponse>> login(@RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response) {
        AuthUserResponse newUser = authService.login(loginUserRequest.email(), loginUserRequest.password(), response);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Login successful",  newUser));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        Map<String, String> accessToken = authService.refresh(refreshToken, response);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Token refreshed successfully",  accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue("refreshToken") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
