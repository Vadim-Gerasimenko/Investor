package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@Table(schema = "auth", name = "tokens",
        indexes = {@Index(name = "idx_auth_tokens_user_id", columnList = "user_id")})
@AllArgsConstructor
@NoArgsConstructor
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "access_token", unique = true, nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", unique = true, nullable = false)
    private String refreshToken;

    @Column(name = "is_logged_out", nullable = false)
    private boolean loggedOut;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}