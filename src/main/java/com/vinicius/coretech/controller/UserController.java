package com.vinicius.coretech.controller;

import com.vinicius.coretech.dto.Response.ApiResponse;
import com.vinicius.coretech.dto.Response.AuthUserResponse;
import com.vinicius.coretech.entity.TokenType;
import com.vinicius.coretech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserResponse>> getUser() {
        return ResponseEntity.ok(new ApiResponse<>("User information found successfully", userService.getUser()));
    }

    @PatchMapping("/update-email")
    public ResponseEntity<Void> updateEmail(@RequestBody Map<String, String> request) {
        userService.updateEmail(request.get("email"), request.get("token"), TokenType.CHANGE_EMAIL);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody Map<String, String> request) {
        userService.updatePassword(request.get("password"), request.get("token"), TokenType.CHANGE_PASSWORD);
        return ResponseEntity.noContent().build();
    }
}
