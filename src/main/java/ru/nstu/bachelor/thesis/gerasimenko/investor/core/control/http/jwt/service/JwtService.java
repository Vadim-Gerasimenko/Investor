package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.AuthTokenService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.secret-key}")
    @NotBlank
    private String secretKey;

    @Value("${security.jwt.access-token-expiration}")
    private long accessTokenExpiration = 36000000;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration = 252000000;

    private final AuthTokenService authTokenService;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpiration);
    }

    private String generateToken(User user, long expiryTime) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSigningKey())
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isAccessTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        JwtParserBuilder parser = Jwts.parser();
        parser.verifyWith(getSigningKey());

        return parser.build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValidAccess(String token, UserDetails user) {
        return extractEmail(token).equals(user.getUsername())
                && isAccessTokenExpired(token)
                && authTokenService.isValidAccessToken(token);
    }

    public boolean isValidRefresh(String token, User user) {
        return extractEmail(token).equals(user.getUsername())
                && isAccessTokenExpired(token)
                && authTokenService.isValidRefreshToken(token);
    }
}