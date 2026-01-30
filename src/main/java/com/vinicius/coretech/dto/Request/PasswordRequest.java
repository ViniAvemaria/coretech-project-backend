package com.vinicius.coretech.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordRequest(
        @NotBlank
        @Size(min = 8)
        String password,

        @NotBlank
        String token
) {
}
