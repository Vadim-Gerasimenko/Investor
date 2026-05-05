package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.auth.AuthTokenRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.AuthToken;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    @Override
    public AuthToken createAuthToken(AuthToken token) {
        return authTokenRepository.save(token);
    }

    @Override
    public AuthToken updateAuthToken(AuthToken token) {
        return authTokenRepository.save(token);
    }

    public List<AuthToken> updateAuthTokens(List<AuthToken> tokens) {
        return authTokenRepository.saveAll(tokens);
    }

    @Override
    public void revokeAuthToken(AuthToken token) {
        token.setLoggedOut(true);
        updateAuthToken(token);
    }

    @Override
    public void revokeAuthTokens(Long userId) {
        List<AuthToken> tokens = findAllActiveTokens(userId);
        tokens.forEach(token -> token.setLoggedOut(true));
        updateAuthTokens(tokens);
    }

    @Override
    public Optional<AuthToken> findByAccessToken(String token) {
        return authTokenRepository.findByAccessToken(token);
    }

    @Override
    public Optional<AuthToken> findByRefreshToken(String token) {
        return authTokenRepository.findByRefreshToken(token);
    }

    @Override
    public List<AuthToken> findAllActiveTokens(Long userId) {
        return authTokenRepository.findAllActiveTokensByUser(userId);
    }

    public boolean isValidAccessToken(String token) {
        return authTokenRepository.findByAccessToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);
    }

    public boolean isValidRefreshToken(String token) {
        return authTokenRepository.findByRefreshToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);
    }
}