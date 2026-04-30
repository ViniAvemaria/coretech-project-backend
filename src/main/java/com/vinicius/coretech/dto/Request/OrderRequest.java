package com.vinicius.coretech.dto.Request;

import com.vinicius.coretech.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
        @NotNull
        Long addressId,

        @NotNull
        PaymentMethod paymentMethod,

        @NotEmpty
        List<OrderItemRequest> items
) {}
