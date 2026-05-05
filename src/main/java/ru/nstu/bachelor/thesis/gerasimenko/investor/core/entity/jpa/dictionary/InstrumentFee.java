package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "dictionary", name = "instruments_fees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentFee {

    @EmbeddedId
    private InstrumentsFeesId id;

    @Column(name = "percent_nano", nullable = false)
    private Long percentNano;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tariff")
    @JoinColumn(name = "tariff", referencedColumnName = "tariff")
    private Tariff tariff;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("instrumentType")
    @JoinColumn(name = "instrument_type", referencedColumnName = "type")
    private InstrumentType instrumentType;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstrumentsFeesId {

        @Column(name = "tariff", length = 20, nullable = false)
        private String tariff;

        @Column(name = "instrument_type", length = 20, nullable = false)
        private String instrumentType;
    }
}