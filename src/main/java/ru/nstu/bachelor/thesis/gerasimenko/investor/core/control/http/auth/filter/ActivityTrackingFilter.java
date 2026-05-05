package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.UserService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityTrackingFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        log.info("to activity tracking filter");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        chain.doFilter(request, response);
        if (auth != null && auth.getPrincipal() instanceof User user) {
            userService.updateLastActivity(user);
        }

        log.info("from activity tracking filter");
    }
}