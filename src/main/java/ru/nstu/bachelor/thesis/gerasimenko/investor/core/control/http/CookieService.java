package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.InvestorCoreConfig;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CookieService {

    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final InvestorCoreConfig config;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration = 252000000;

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(!config.isDevMode())
                .path("/api/auth")
                .maxAge(Duration.ofMillis(refreshTokenExpiration))
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(!config.isDevMode())
                .path("/api/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}