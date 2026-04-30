package com.vinicius.coretech.dto.Request;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(

        @NotBlank
        String street,

        @NotBlank
        String number,

        String complement,
        String neighborhood,

        @NotBlank
        String city,

        @NotBlank
        String state,

        @NotBlank
        String zipCode,

        @NotBlank
        String country
) {}
