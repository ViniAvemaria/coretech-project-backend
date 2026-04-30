package com.vinicius.coretech.controller;

import com.vinicius.coretech.dto.Request.AddressRequest;
import com.vinicius.coretech.dto.Request.EmailRequest;
import com.vinicius.coretech.dto.Request.NameRequest;
import com.vinicius.coretech.dto.Request.PasswordRequest;
import com.vinicius.coretech.dto.Request.ValidationTokenRequest;
import com.vinicius.coretech.dto.Response.AddressResponse;
import com.vinicius.coretech.dto.Response.ApiResponse;
import com.vinicius.coretech.dto.Response.AuthUserResponse;
import com.vinicius.coretech.entity.enums.TokenType;
import com.vinicius.coretech.service.TokenService;
import com.vinicius.coretech.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        userService.deleteUser(request.token(), TokenType.DELETE_ACCOUNT, request.id());
        tokenService.clearTokens(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses() {
        return ResponseEntity.ok(new ApiResponse<>("Addresses found successfully", userService.getAddresses()));
    }

    @PostMapping("/addresses")
    public ResponseEntity<Void> createAddress(@Valid @RequestBody AddressRequest request) {
        userService.createAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<Void> updateAddress(@Min(1) @PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        userService.updateAddress(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@Min(1) @PathVariable Long id) {
        userService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
