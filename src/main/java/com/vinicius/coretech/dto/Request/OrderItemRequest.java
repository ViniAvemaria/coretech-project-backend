package com.vinicius.coretech.dto.Request;

import jakarta.validation.constraints.Min;

public record OrderItemRequest(
        @Min(1)
        Long productId,

        @Min(1)
        int quantity
) {}
