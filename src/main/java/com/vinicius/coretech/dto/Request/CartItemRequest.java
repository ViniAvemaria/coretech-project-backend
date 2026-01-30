package com.vinicius.coretech.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotNull
        Long id,

        @Min(1)
        int quantity
) {
}
