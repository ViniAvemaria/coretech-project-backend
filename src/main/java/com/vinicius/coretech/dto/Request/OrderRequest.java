package com.vinicius.coretech.dto.Request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
        @NotEmpty
        List<OrderItemRequest> items
) {}
