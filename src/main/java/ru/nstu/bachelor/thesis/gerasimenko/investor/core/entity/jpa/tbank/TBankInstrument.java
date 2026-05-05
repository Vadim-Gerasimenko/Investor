package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank;

import jakarta.persistence.*;
import lombok.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.InstrumentType;

@Builder
@Getter
@Setter
@Entity
@Table(schema = "tbank", name = "instruments",
        indexes = {
                @Index(name = "idx_tbank_instruments_figi", columnList = "figi", unique = true),
                @Index(name = "idx_tbank_instruments_instrument_type", columnList = "instrument_type")
        })
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TBankInstrument {

    @Id
    @Column(name = "uid", length = 36)
    private String uid;

    @Column(name = "figi", length = 30, unique = true, nullable = false)
    private String figi;

    @Column(name = "ticker", length = 20)
    private String ticker;

    @Column(name = "isin", length = 20)
    private String isin;

    @Column(name = "lot", nullable = false)
    private Integer lot;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_type", nullable = false)
    private InstrumentType instrumentType;
}