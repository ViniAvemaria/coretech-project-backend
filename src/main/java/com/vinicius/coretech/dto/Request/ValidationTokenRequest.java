package com.vinicius.coretech.dto.Request;

import jakarta.validation.constraints.NotBlank;

public record ValidationTokenRequest(
        @NotBlank
        String token
) {
}
