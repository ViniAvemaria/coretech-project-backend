package com.vinicius.coretech.DTO.Request;

public record LoginUserRequest(
        String email,
        String password
) {
}
