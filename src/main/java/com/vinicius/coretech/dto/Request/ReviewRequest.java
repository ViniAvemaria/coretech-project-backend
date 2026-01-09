package com.vinicius.coretech.dto.Request;

public record ReviewRequest(
        String comment,
        Double rating
) {
}
