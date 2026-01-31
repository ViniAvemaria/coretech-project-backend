package com.vinicius.coretech.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ValidationTokenRequest(
        @Min(1)
        Long id,

        @NotBlank
        String token
) {
}
