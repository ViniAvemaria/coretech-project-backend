package com.vinicius.coretech.dto.Request;

public record LoginUserRequest(
        String email,
        String password
) {
}
