package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank;

import jakarta.persistence.*;
import lombok.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

@Entity
@Table(schema = "tbank", name = "active_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TBankActiveToken {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false, unique = true)
    private TBankToken token;
}