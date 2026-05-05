package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.jwt.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.AuthTokenService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.HttpHeaders;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final AuthTokenService authTokenService;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        log.debug("Logout request received");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION_HEADER);

        if (Objects.isNull(authHeader) || !authHeader.startsWith(HttpHeaders.BEARER_TOKEN)) {
            return;
        }

        String token = authHeader.substring(HttpHeaders.BEARER_TOKEN.length());
        authTokenService.findByAccessToken(token).ifPresent(authToken -> {
            authToken.setLoggedOut(true);
            authTokenService.updateAuthToken(authToken);
        });
    }
}