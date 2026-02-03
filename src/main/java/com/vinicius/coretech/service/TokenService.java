package com.vinicius.coretech.service;

import com.vinicius.coretech.entity.RefreshToken;
import com.vinicius.coretech.entity.TokenType;
import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.entity.VerificationToken;
import com.vinicius.coretech.exception.BadRequestException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.repository.RefreshTokenRepository;
import com.vinicius.coretech.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.access-token.expiration-minutes}")
    private long accessTokenExpirationMinutes;

    @Value("${jwt.refresh-token.expiration-days}")
    private long refreshTokenExpirationDays;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.same-site}")
    private String cookieSameSite;

    public void generateTokens(Authentication auth, HttpServletResponse response, User user) {
        Instant now = Instant.now();

        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES))
                .subject(auth.getName())
                .claim("roles", scope)
                .build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .domain(".coretechstore.dedyn.io")
                .maxAge(accessTokenExpirationMinutes * 60)
                .sameSite(cookieSameSite)
                .build();
        response.addHeader("Set-Cookie", accessCookie.toString());

        Instant refreshTokenExpiration = now.plus(refreshTokenExpirationDays, ChronoUnit.DAYS);

        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(refreshTokenExpiration)
                .subject(auth.getName())
                .build();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .domain(".coretechstore.dedyn.io")
                .maxAge(refreshTokenExpirationDays * 24 * 60 * 60)
                .sameSite(cookieSameSite)
                .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(refreshToken)
                        .expiresAt(refreshTokenExpiration)
                        .user(user)
                        .build()
        );
    }

    public void clearTokens(HttpServletResponse response) {
        ResponseCookie accessToken = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .domain(".coretechstore.dedyn.io")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        response.addHeader("Set-Cookie", accessToken.toString());

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .domain(".coretechstore.dedyn.io")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        response.addHeader("Set-Cookie", refreshToken.toString());
    }

    public VerificationToken validateRecoveryToken(String token, Long id) {
        VerificationToken recoveryToken = verificationTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recovery Token not found"));

        if(recoveryToken.getExpiresAt().isBefore(Instant.now()) || recoveryToken.isUsed()) {
            throw new BadRequestException("Recovery token expired or used");
        }

        if(recoveryToken.getTokenType() != TokenType.RESET_PASSWORD || !passwordEncoder.matches(token, recoveryToken.getToken())) {
            throw new BadRequestException("Invalid Recovery Token");
        }

        return recoveryToken;
    }

    public VerificationToken validateLinkToken(String token, TokenType tokenType, Long id) {
        VerificationToken verificationToken = verificationTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Link not found"));

        if(verificationToken.getExpiresAt().isBefore(Instant.now()) || verificationToken.isUsed()) {
            throw new BadRequestException("Link expired or used");
        }

        if(verificationToken.getTokenType() != tokenType || !passwordEncoder.matches(token, verificationToken.getToken())) {
            throw new BadRequestException("Invalid link");
        }

        return verificationToken;
    }

    public VerificationToken validateDigitToken(String token, TokenType tokenType, User user) {
        VerificationToken verificationToken = verificationTokenRepository.findByUserAndTokenType(user, tokenType)
                .orElseThrow(() -> new ResourceNotFoundException("Code not found"));

        if(verificationToken.getExpiresAt().isBefore(Instant.now()) || verificationToken.isUsed()) {
            throw new BadRequestException("Code expired or used");
        }

        if(verificationToken.getTokenType() != tokenType || !passwordEncoder.matches(token, verificationToken.getToken())) {
            throw new BadRequestException("Invalid code");
        }

        return verificationToken;
    }
}
