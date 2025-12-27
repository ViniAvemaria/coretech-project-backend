package com.vinicius.coretech.DTO.Response;

import java.time.Instant;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        Instant refreshTokenExpiration
) {
}
