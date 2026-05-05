package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.UserService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.jwt.service.JwtService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.HttpHeaders;

import java.io.IOException;
import java.util.Objects;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("to jwt auth filter");

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION_HEADER);

        if (isNull(authHeader) || !authHeader.startsWith(HttpHeaders.BEARER_TOKEN)) {
            log.info("jwt auth token not present");
            filterChain.doFilter(request, response);

            log.info("from jwt auth filter");
            return;
        }

        String token = authHeader.substring(HttpHeaders.BEARER_TOKEN.length());
        String email = jwtService.extractEmail(token);

        if (Objects.nonNull(email) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            UserDetails userDetails = userService.loadUserByUsername(email);

            if (jwtService.isValidAccess(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.info("jwt auth token expired");
            }
        }

        log.info("from jwt auth filter"); //TODO: check this
        filterChain.doFilter(request, response);
    }
}