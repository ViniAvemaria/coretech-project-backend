package com.vinicius.coretech.service;

import com.vinicius.coretech.dto.Response.AuthUserResponse;
import com.vinicius.coretech.entity.TokenType;
import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.entity.VerificationToken;
import com.vinicius.coretech.exception.ForbiddenException;
import com.vinicius.coretech.repository.UserRepository;
import com.vinicius.coretech.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final TokenService tokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public AuthUserResponse getUser() {
        return AuthUserResponse.from(securityService.getUserFromSecurityContext());
    }

    @Transactional
    public void updateEmail(String email, String token, TokenType tokenType) {
        User user = securityService.getUserFromSecurityContext();

        VerificationToken verificationToken = tokenService.validateDigitToken(token, tokenType, user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        user.setEmail(email);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String password, String token, TokenType tokenType) {
        User user = securityService.getUserFromSecurityContext();

        VerificationToken verificationToken = tokenService.validateDigitToken(token, tokenType, user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public void updateName(String firstName, String lastName) {
        User user = securityService.getUserFromSecurityContext();

        user.setFirstName(firstName);
        user.setLastName(lastName);

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String token,  TokenType tokenType, Long id, HttpServletResponse response) {
        User user = securityService.getUserFromSecurityContext();

        VerificationToken deleteToken = tokenService.validateLinkToken(token, tokenType, id);

        if(!user.equals(deleteToken.getUser())) {
            throw new ForbiddenException("You are not allowed to do this action");
        }

        deleteToken.setUsed(true);
        verificationTokenRepository.save(deleteToken);

        userRepository.delete(user);
    }
}
