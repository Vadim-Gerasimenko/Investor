package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.UserUnauthorizedException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.jwt.service.JwtService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.AuthTokenService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.RoleService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.UserService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.HttpHeaders;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.LoginRequestDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.RegisterRequestDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.AuthToken;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.UserProfile;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthTokenService authTokenService;
    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    @Transactional
    public User register(RegisterRequestDto request) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(roleService.findDefaultRole()))
                .registeredAt(LocalDateTime.now())
                .build();

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .birthDate(request.profile().getBirthDate())
                .phoneNumber(request.profile().getPhoneNumber())
                .firstName(request.profile().getFirstName())
                .middleName(request.profile().getMiddleName())
                .lastName(request.profile().getLastName())
                .build();

        user.setProfile(userProfile);
        return userService.create(user);
    }

    public AuthToken authenticate(LoginRequestDto request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userService.findByEmail(request.email());
        userService.login(user);

        authTokenService.revokeAuthTokens(user.getId());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return authTokenService.createAuthToken(AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build());
    }

    public AuthToken refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION_HEADER);

        if (Objects.isNull(authHeader) || !authHeader.startsWith(HttpHeaders.BEARER_TOKEN)) {
            throw new UserUnauthorizedException("User is not authorized");
        }

        String token = authHeader.substring(HttpHeaders.BEARER_TOKEN.length());
        String email = jwtService.extractEmail(token);
        User user = userService.findByEmail(email);

        if (!jwtService.isValidRefresh(token, user)) {
            throw new UserUnauthorizedException(String.format("User is not authorized to refresh token: email=[%s]", email));
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        authTokenService.revokeAuthTokens(user.getId());
        return authTokenService.updateAuthToken(AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build());
    }
}