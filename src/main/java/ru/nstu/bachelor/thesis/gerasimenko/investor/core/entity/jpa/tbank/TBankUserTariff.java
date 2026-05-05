package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.Tariff;

@Entity
@Table(schema = "tbank", name = "users_tariffs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TBankUserTariff {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff", nullable = false)
    private Tariff tariff;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}