package com.vinicius.coretech.DTO.Response;

import java.time.Instant;

public record TokenPairResponse(
        String accessToken,
        String refreshToken,
        Instant refreshTokenExpiration
) {
}
