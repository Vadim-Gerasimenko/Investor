package ru.nstu.bachelor.thesis.gerasimenko.investor.core.boundary;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.AuthConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.UserConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.CookieService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.auth.AuthService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.AuthResponseDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.LoginRequestDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.RegisterRequestDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.UserProfileDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.AuthToken;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticate(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        log.debug("Authenticating request received");
        AuthToken authToken = authService.authenticate(request);
        ResponseCookie refreshCookie = cookieService.createRefreshTokenCookie(authToken.getRefreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return ResponseEntity.ok(AuthConverter.convert(authToken));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserProfileDto> register(@RequestBody RegisterRequestDto request) {
        log.debug("Register request received");
        return ResponseEntity.ok(UserConverter.convert(authService.register(request)));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthResponseDto> refreshToken(@CookieValue(name = "refresh_token", required = false)
                                                        String refreshToken,
                                                        HttpServletResponse response) {
        log.debug("Refresh token request received");

        if (refreshToken == null) {
            throw new InvestorCoreException("Refresh token not found");
        }

        AuthToken authToken = authService.refreshTokens(refreshToken);
        ResponseCookie refreshCookie = cookieService.createRefreshTokenCookie(authToken.getRefreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return ResponseEntity.ok(AuthConverter.convert(authToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        log.debug("Logout request received");
        ResponseCookie clearCookie = cookieService.clearRefreshTokenCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
        return ResponseEntity.ok().build();
    }
}