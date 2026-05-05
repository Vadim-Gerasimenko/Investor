package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.AuthToken;

import java.util.List;
import java.util.Optional;

public interface AuthTokenService {

    AuthToken createAuthToken(AuthToken token);

    AuthToken updateAuthToken(AuthToken token);

    List<AuthToken> updateAuthTokens(List<AuthToken> tokens);

    void revokeAuthToken(AuthToken token);

    void revokeAuthTokens(Long userId);

    Optional<AuthToken> findByAccessToken(String token);

    Optional<AuthToken> findByRefreshToken(String token);

    List<AuthToken> findAllActiveTokens(Long userId);

    boolean isValidAccessToken(String token);

    boolean isValidRefreshToken(String token);
}