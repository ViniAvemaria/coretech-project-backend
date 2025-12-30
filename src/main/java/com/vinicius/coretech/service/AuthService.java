package com.vinicius.coretech.service;

import com.vinicius.coretech.DTO.Response.AuthUserResponse;
import com.vinicius.coretech.DTO.Response.TokenResponse;
import com.vinicius.coretech.entity.RefreshToken;
import com.vinicius.coretech.entity.Role;
import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.exception.RoleNotFoundException;
import com.vinicius.coretech.exception.UnauthorizedException;
import com.vinicius.coretech.repository.RefreshTokenRepository;
import com.vinicius.coretech.repository.RoleRepository;
import com.vinicius.coretech.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public void register(String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        Role userRole = roleRepository.findByAuthority("USER")
                .orElseThrow(() -> new RoleNotFoundException("Default role USER not found"));

        userRepository.save(User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .authorities(Set.of(userRole))
                .build());
    }

    public AuthUserResponse login(String email, String password, HttpServletResponse response) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);
            for (RefreshToken token : activeTokens) {
                token.setRevoked(true);
            }
            refreshTokenRepository.saveAll(activeTokens);

            TokenResponse tokens = tokenService.generateTokens(auth, response);
            refreshTokenRepository.save(
                    RefreshToken.builder()
                            .token(tokens.refreshToken())
                            .expiresAt(tokens.refreshTokenExpiration())
                            .user(user)
                            .build()
            );

            deleteRevokedTokens(refreshTokenRepository.findAllByUserOrderByCreatedAtAsc(user));

            return AuthUserResponse.from(user, tokens.accessToken());
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    public Map<String, String> refresh(String token, HttpServletResponse response) {
        Map<String, String> accessTokenResponse = new HashMap<>();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now()) || refreshToken.isRevoked()) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        User user = refreshToken.getUser();

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                user.getAuthorities()
        );
        TokenResponse tokens = tokenService.generateTokens(auth, response);
        accessTokenResponse.put("accessToken", tokens.accessToken());

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(tokens.refreshToken())
                .user(user)
                .expiresAt(tokens.refreshTokenExpiration())
                .build();
        refreshTokenRepository.save(newRefreshToken);

        deleteRevokedTokens(refreshTokenRepository.findAllByUserOrderByCreatedAtAsc(user));

        return accessTokenResponse;
    }

    public void logout(String token, HttpServletResponse response) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        tokenService.invalidateToken(response);

        deleteRevokedTokens(refreshTokenRepository.findAllByUserOrderByCreatedAtAsc(refreshToken.getUser()));
    }

    private void deleteRevokedTokens(List<RefreshToken> userTokens) {
        if (userTokens.size() > 3) {
            List<RefreshToken> toDelete = userTokens.stream()
                    .filter(RefreshToken::isRevoked)
                    .limit(userTokens.size() - 3)
                    .toList();
            refreshTokenRepository.deleteAll(toDelete);
        }
    }
}
