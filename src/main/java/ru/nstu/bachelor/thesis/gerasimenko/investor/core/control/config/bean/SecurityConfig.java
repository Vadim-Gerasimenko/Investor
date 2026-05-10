package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.auth.filter.ActivityTrackingFilter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.jwt.filter.JwtFilter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.jwt.handler.JwtAccessDeniedHandler;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.jwt.handler.JwtLogoutHandler;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.UserService;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    private final JwtFilter jwtFilter;
    private final ActivityTrackingFilter activityTrackingFilter;

    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtLogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh_token", "/")
                            .permitAll();
                    auth.requestMatchers("/admin/**").hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .userDetailsService(userService)
                .exceptionHandling(e -> {
                    e.accessDeniedHandler(accessDeniedHandler);
                    e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                })
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(activityTrackingFilter, JwtFilter.class)
                .logout(log -> {
                    log.logoutUrl("/logout");
                    log.addLogoutHandler(logoutHandler);
                    log.logoutSuccessHandler((request, response, authentication) ->
                            SecurityContextHolder.clearContext());
                }).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}