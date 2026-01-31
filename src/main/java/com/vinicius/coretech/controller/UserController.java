package com.vinicius.coretech.controller;

import com.vinicius.coretech.dto.Request.EmailRequest;
import com.vinicius.coretech.dto.Request.NameRequest;
import com.vinicius.coretech.dto.Request.PasswordRequest;
import com.vinicius.coretech.dto.Request.ValidationTokenRequest;
import com.vinicius.coretech.dto.Response.ApiResponse;
import com.vinicius.coretech.dto.Response.AuthUserResponse;
import com.vinicius.coretech.entity.TokenType;
import com.vinicius.coretech.service.TokenService;
import com.vinicius.coretech.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserResponse>> getUser() {
        return ResponseEntity.ok(new ApiResponse<>("User information found successfully", userService.getUser()));
    }

    @PatchMapping("/update-email")
    public ResponseEntity<Void> updateEmail(@Valid @RequestBody EmailRequest request) {
        userService.updateEmail(request.email(), request.token(), TokenType.CHANGE_EMAIL);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordRequest request) {
        userService.updatePassword(request.password(), request.token(), TokenType.CHANGE_PASSWORD);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-name")
    public ResponseEntity<Void> updateName(@Valid @RequestBody NameRequest request) {
        userService.updateName(request.firstName(), request.lastName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody ValidationTokenRequest request, HttpServletResponse response) {
        userService.deleteUser(request.token(), TokenType.DELETE_ACCOUNT, request.id(), response);
        tokenService.clearTokens(response);
        return ResponseEntity.noContent().build();
    }
}
