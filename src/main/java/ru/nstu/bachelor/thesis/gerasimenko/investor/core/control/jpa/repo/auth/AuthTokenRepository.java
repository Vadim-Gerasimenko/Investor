package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.AuthToken;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    @Query("SELECT t FROM AuthToken t WHERE t.user.id = :userId AND t.loggedOut = false")
    List<AuthToken> findAllActiveTokensByUser(Long userId);

    Optional<AuthToken> findByAccessToken(String accessToken);

    Optional<AuthToken> findByRefreshToken(String refreshToken);
}