package com.vinicius.coretech.controller;

import com.vinicius.coretech.dto.Response.ApiResponse;
import com.vinicius.coretech.dto.Response.AuthUserResponse;
import com.vinicius.coretech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserResponse>> getUser() {
        return ResponseEntity.ok(new ApiResponse<>("User information found successfully", userService.getUser()));
    }
}
