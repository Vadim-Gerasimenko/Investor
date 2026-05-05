package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(schema = "tbank", name = "instrument_prices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TBankInstrumentPrice {

    @Id
    @Column(name = "instrument_uid", length = 36)
    private String instrumentUid;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
}