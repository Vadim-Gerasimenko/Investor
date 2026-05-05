package ru.nstu.bachelor.thesis.gerasimenko.investor.core.boundary;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.AuthConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.UserConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.auth.AuthService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.AuthResponseDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.LoginRequestDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.RegisterRequestDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.UserProfileDto;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticate(@RequestBody LoginRequestDto request) {
        log.info("Authenticating request received: {}", request);
        return ResponseEntity.ok(AuthConverter.convert(authService.authenticate(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileDto> register(@RequestBody RegisterRequestDto request) {
        log.info("Register request received: {}", request);
        return ResponseEntity.ok(UserConverter.convert(authService.register(request)));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthResponseDto> refreshToken(HttpServletRequest request) {
        log.info("Refresh token request received: {}", request);
        return ResponseEntity.ok(AuthConverter.convert(authService.refreshToken(request)));
    }
}