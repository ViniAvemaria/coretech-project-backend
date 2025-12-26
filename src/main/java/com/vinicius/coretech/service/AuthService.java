package com.vinicius.coretech.service;

import com.vinicius.coretech.DTO.Response.AuthUserResponse;
import com.vinicius.coretech.entity.Role;
import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.repository.RoleRepository;
import com.vinicius.coretech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public String register(String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already exists");
        }

        Role userRole = roleRepository.findByAuthority("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

        userRepository.save(User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .authorities(Set.of(userRole))
                .build());

        return "User registered successfully";
    }

    public AuthUserResponse login(String email, String password) {

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found"));

            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            String token = tokenService.generateJwt(auth);

            return AuthUserResponse.from(user, token);
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }
    }
}
