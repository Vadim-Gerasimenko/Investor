package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank;

import jakarta.persistence.*;
import lombok.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.AccountAccessLevel;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.AccountStatus;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.AccountType;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@Table(schema = "tbank", name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_auth_user_acc_name",
                        columnNames = {"user_id", "acc_name"})
        },
        indexes = {
                @Index(name = "idx_tbank_accounts_user_id", columnList = "user_id"),
                @Index(name = "idx_tbank_accounts_user_active", columnList = "user_id, is_active"),
                @Index(name = "idx_tbank_accounts_type", columnList = "type"),
                @Index(name = "idx_tbank_accounts_status", columnList = "status"),
                @Index(name = "idx_tbank_accounts_opened_date", columnList = "opened_date")
        })
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TBankAccount {

    @Id
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "acc_name", nullable = false)
    private String accountName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type", nullable = false)
    private AccountType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", nullable = false)
    private AccountStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_level", nullable = false)
    private AccountAccessLevel accessLevel;

    @Column(name = "opened_date", nullable = false)
    private LocalDateTime openedDate;

    @Column(name = "closed_date")
    private LocalDateTime closedDate;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}