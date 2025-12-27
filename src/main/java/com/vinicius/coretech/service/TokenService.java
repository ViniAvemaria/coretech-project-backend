package com.vinicius.coretech.service;

import com.vinicius.coretech.DTO.Response.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.access-token.expiration-minutes}")
    private long ACCESS_TOKEN_EXPIRATION_MINUTES;

    @Value("${jwt.refresh-token.expiration-days}")
    private long REFRESH_TOKEN_EXPIRATION_DAYS;

    public TokenResponse generateTokens(Authentication auth, HttpServletResponse response) {
        Instant now = Instant.now();

        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Access Token
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES))
                .subject(auth.getName())
                .claim("roles", scope)
                .build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

        // Refresh Token
        Instant refreshTokenExpiration = now.plus(REFRESH_TOKEN_EXPIRATION_DAYS, ChronoUnit.DAYS);
        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(refreshTokenExpiration)
                .subject(auth.getName())
                .build();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

        // Add refresh token as HttpOnly cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api")
                .maxAge(REFRESH_TOKEN_EXPIRATION_DAYS * 24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return new TokenResponse(accessToken, refreshToken, refreshTokenExpiration);
    }
}
