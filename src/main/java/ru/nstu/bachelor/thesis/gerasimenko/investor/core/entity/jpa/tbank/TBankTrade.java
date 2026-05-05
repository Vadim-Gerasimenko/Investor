package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(schema = "tbank", name = "trades",
        indexes = {
                @Index(name = "idx_tbank_trades_operation_id", columnList = "operation_id"),
                @Index(name = "idx_tbank_trades_trade_date", columnList = "trade_date DESC")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TBankTrade {

    @Id
    @Column(name = "trade_id", length = 36)
    private String tradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private TBankOperation operation;

    @Column(name = "trade_date", nullable = false)
    private LocalDateTime tradeDate;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "price_value", nullable = false)
    private Long priceValue;

    @Column(name = "price_currency", length = 3)
    private String priceCurrency;
}