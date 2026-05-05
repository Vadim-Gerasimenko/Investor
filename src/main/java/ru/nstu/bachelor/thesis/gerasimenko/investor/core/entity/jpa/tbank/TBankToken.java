package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(schema = "tbank", name = "tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tbank_user_token_name",
                        columnNames = {"user_id", "token_name"})
        },
        indexes = {
                @Index(name = "idx_tbank_tokens_token", columnList = "token", unique = true),
                @Index(name = "idx_tbank_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_tbank_tokens_is_active", columnList = "is_active")
        })
@AllArgsConstructor
@NoArgsConstructor
public class TBankToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "token_name", nullable = false)
    private String tokenName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TBankToken(User user, String token, String tokenName) {
        this.user = user;
        this.token = token;
        this.tokenName = tokenName;
        this.createdAt = LocalDateTime.now();
    }
}