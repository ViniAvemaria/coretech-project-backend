package com.vinicius.coretech.dto.Request;

import jakarta.validation.constraints.NotBlank;

public record NameRequest(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {
}
