package com.vinicius.coretech.dto.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        String comment,

        @NotNull
        @DecimalMin("1.0")
        @DecimalMax("5.0")
        Double rating
) {
}
