package com.vinicius.coretech.dto.Request;

import com.vinicius.coretech.entity.PhotoCredit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record ProductRequest(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        @Positive
        Double price,

        @PositiveOrZero
        Double rating,

        @NotBlank
        String image,

        @NotNull
        @Min(0)
        Integer stockQuantity,

        @NotEmpty
        List<@NotBlank String> specifications,

        @NotNull
        PhotoCredit photoCredit,

        @NotBlank
        String category
) {}
