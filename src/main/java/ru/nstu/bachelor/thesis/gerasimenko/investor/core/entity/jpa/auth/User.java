package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankToken;

import java.time.LocalDateTime;
import java.util.*;

@Builder
@Entity
@Table(schema = "auth", name = "users",
        indexes = {
                @Index(name = "idx_auth_users_email", columnList = "email"),
                @Index(name = "idx_auth_users_registered_at", columnList = "registered_at")
        })
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"profile", "roles", "authTokens", "tBankTokens"})
@ToString(exclude = {"profile", "roles", "authTokens", "tBankTokens"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserProfile profile;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Column(name = "last_login_at", nullable = false)
    private LocalDateTime lastLoginAt;

    @Column(name = "last_activity_at", nullable = false)
    private LocalDateTime lastActivityAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            schema = "auth", name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<AuthToken> authTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<TBankToken> tBankTokens = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }
}