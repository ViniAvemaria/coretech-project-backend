package com.vinicius.coretech.DTO.Response;

import com.vinicius.coretech.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public record AuthUserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<String> roles,
        String accessToken
) {

    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()),
                null
        );
    }

    public static AuthUserResponse from(User user, String accessToken) {
        return new AuthUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()),
                accessToken
        );
    }
}
