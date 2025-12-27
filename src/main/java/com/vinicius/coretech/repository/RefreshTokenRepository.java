package com.vinicius.coretech.repository;

import com.vinicius.coretech.entity.RefreshToken;
import com.vinicius.coretech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUserOrderByCreatedAtAsc(User user);
    List<RefreshToken> findAllByUserAndRevokedFalse(User user);
}
