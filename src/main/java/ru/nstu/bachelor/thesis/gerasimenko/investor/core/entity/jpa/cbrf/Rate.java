package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.cbrf;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(schema = "cbrf", name = "rates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rate {

    @EmbeddedId
    private RateId id;

    @Column(name = "rate", nullable = false)
    private Long rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("currencyFrom")
    @JoinColumn(name = "currency_from", referencedColumnName = "code_a3", nullable = false)
    private Currency currencyFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("currencyTo")
    @JoinColumn(name = "currency_to", referencedColumnName = "code_a3", nullable = false)
    private Currency currencyTo;

    @Builder
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RateId implements Serializable {

        @Column(name = "currency_from", length = 3, nullable = false)
        private String currencyFrom;

        @Column(name = "currency_to", length = 3, nullable = false)
        private String currencyTo;

        @Column(name = "start_date", nullable = false)
        private LocalDate startDate;
    }
}