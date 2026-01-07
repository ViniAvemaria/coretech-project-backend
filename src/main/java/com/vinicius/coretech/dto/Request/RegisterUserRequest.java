package com.vinicius.coretech.dto.Request;

public record RegisterUserRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
