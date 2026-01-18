package com.vinicius.coretech.service;

import com.vinicius.coretech.entity.Cart;
import com.vinicius.coretech.entity.RefreshToken;
import com.vinicius.coretech.entity.Role;
import com.vinicius.coretech.entity.User;
import com.vinicius.coretech.entity.VerificationToken;
import com.vinicius.coretech.exception.BadRequestException;
import com.vinicius.coretech.exception.ConflictException;
import com.vinicius.coretech.exception.ResourceNotFoundException;
import com.vinicius.coretech.exception.RoleNotFoundException;
import com.vinicius.coretech.exception.UnauthorizedException;
import com.vinicius.coretech.repository.CartRepository;
import com.vinicius.coretech.repository.RefreshTokenRepository;
import com.vinicius.coretech.repository.RoleRepository;
import com.vinicius.coretech.repository.UserRepository;
import com.vinicius.coretech.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.vinicius.coretech.entity.TokenType.CONFIRM_EMAIL;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Value("${token.confirm.expiration-hours}")
    private long confirmTokenExpirationHours;

    @Value("${email.service.enabled}")
    private boolean emailServiceEnabled;

    public void register(String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        Role userRole = roleRepository.findByAuthority("USER")
                .orElseThrow(() -> new RoleNotFoundException("Default role USER not found"));

        User user = userRepository.save(User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .authorities(Set.of(userRole))
                .enabled(!emailServiceEnabled)
                .build());

        cartRepository.save(Cart.builder()
                .user(user)
                .build());

        if(emailServiceEnabled) {
            String confirmationToken = UUID.randomUUID().toString();

            VerificationToken newToken = verificationTokenRepository.save(VerificationToken.builder()
                    .token(passwordEncoder.encode(confirmationToken))
                    .tokenType(CONFIRM_EMAIL)
                    .user(user)
                    .expiresAt(Instant.now().plus(confirmTokenExpirationHours, ChronoUnit.HOURS))
                    .build());

            mailService.sendConfirmationToken(user.getEmail(), confirmationToken, newToken.getId());
        }
    }

    public void confirmEmail(String token, Long id){
        VerificationToken verificationToken = verificationTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Confirmation Token not found"));

        if(verificationToken.getExpiresAt().isBefore(Instant.now()) || verificationToken.isUsed()) {
            throw new BadRequestException("Confirmation token expired or used");
        }

        if(verificationToken.getTokenType() != CONFIRM_EMAIL) {
            throw new BadRequestException("Invalid Confirmation Token");
        }

        if(!passwordEncoder.matches(token, verificationToken.getToken())) {
            throw new BadRequestException("Invalid Confirmation Token");
        }

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void resendConfirmation(String token, Long id) {
        VerificationToken verificationToken = verificationTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Confirmation Token not found"));

        if(!verificationToken.getExpiresAt().isBefore(Instant.now()) && !verificationToken.isUsed()) {
            throw new ConflictException("There's still a valid Confirmation Token");
        }

        if(!passwordEncoder.matches(token, verificationToken.getToken())) {
            throw new BadRequestException("Invalid Confirmation Token");
        }

        User user = verificationToken.getUser();

        if(user.isEnabled()) {
            throw new ConflictException("This account has already been activated");
        }

        String confirmationToken = UUID.randomUUID().toString();

        VerificationToken newToken = verificationTokenRepository.save(VerificationToken.builder()
                .token(passwordEncoder.encode(confirmationToken))
                .tokenType(CONFIRM_EMAIL)
                .user(user)
                .expiresAt(Instant.now().plus(confirmTokenExpirationHours, ChronoUnit.HOURS))
                .build());

        mailService.sendConfirmationToken(user.getEmail(), confirmationToken, newToken.getId());
    }

    @Transactional(noRollbackFor = DisabledException.class)
    public void login(String email, String password, HttpServletResponse response) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Email or password incorrect"));

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);
            for (RefreshToken token : activeTokens) {
                token.setRevoked(true);
            }
            refreshTokenRepository.saveAll(activeTokens);

            tokenService.generateTokens(auth, response, user);

            deleteRevokedTokens(refreshTokenRepository.findAllByUserOrderByCreatedAtAsc(user));
        } catch (DisabledException e) {
            VerificationToken verificationToken = verificationTokenRepository.findByUser(user)
                    .orElseGet(() -> {
                        String confirmationToken = UUID.randomUUID().toString();

                        VerificationToken newToken = VerificationToken.builder()
                                .token(passwordEncoder.encode(confirmationToken))
                                .tokenType(CONFIRM_EMAIL)
                                .user(user)
                                .expiresAt(Instant.now().plus(confirmTokenExpirationHours, ChronoUnit.HOURS))
                                .build();

                        verificationTokenRepository.save(newToken);
                        mailService.sendConfirmationToken(user.getEmail(), confirmationToken, newToken.getId());
                        return newToken;
                    });

            if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
                String confirmationToken = UUID.randomUUID().toString();

                verificationToken.setToken(passwordEncoder.encode(confirmationToken));
                verificationToken.setExpiresAt(Instant.now().plus(confirmTokenExpirationHours, ChronoUnit.HOURS));

                verificationTokenRepository.save(verificationToken);
                mailService.sendConfirmationToken(user.getEmail(), confirmationToken, verificationToken.getId());
            }

            throw new DisabledException("This account is disabled. Check your email.");
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Email or password incorrect");
        }
    }

    public void refresh(String token, HttpServletResponse response) {
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
        tokenService.generateTokens(auth, response, user);

        deleteRevokedTokens(refreshTokenRepository.findAllByUserOrderByCreatedAtAsc(user));
    }

    public void logout(String token, HttpServletResponse response) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        tokenService.clearTokens(response);

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
