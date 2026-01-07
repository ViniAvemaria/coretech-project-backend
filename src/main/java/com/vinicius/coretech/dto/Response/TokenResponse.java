package com.vinicius.coretech.dto.Response;

import java.time.Instant;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        Instant refreshTokenExpiration
) {
}
