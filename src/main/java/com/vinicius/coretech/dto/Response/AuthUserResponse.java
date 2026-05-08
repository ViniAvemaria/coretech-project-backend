package com.vinicius.coretech.dto.Response;

import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.entity.enums.AuthProvider;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public record AuthUserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        AuthProvider authProvider,
        Set<String> roles
) {

    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAuthProvider(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
        );
    }
}
